package com.astrsomn.server.plugin.registry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.api.runtime.common.constant.SystemExtensionEnum;
import com.astrsomn.api.runtime.common.entity.SystemExtensionEntity;
import com.astrsomn.api.runtime.common.langchain.extension.AstroExtensionDescriptor;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.mapper.AstSystemExtensionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class SystemExtensionRegistry implements ApplicationListener<ApplicationReadyEvent> {

    private static final String LOG_PREFIX = "[Astrsomn] [扩展注册中心] ====> ";

    private ApplicationContext applicationContext;
    private AstSystemExtensionMapper astSystemExtensionMapper;
    
    @Autowired(required = false)
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Autowired(required = false)
    public void setSystemExtensionMapper(AstSystemExtensionMapper astSystemExtensionMapper) {
        this.astSystemExtensionMapper = astSystemExtensionMapper;
    }

    /**
     * 合并 Spring Bean 和 SPI 加载的扩展描述符（Bean 优先）
     */
    public static Map<String, AstroExtensionDescriptor> mergeDescriptors(ApplicationContext applicationContext) {
        Stream<AstroExtensionDescriptor> beanStream = applicationContext.getBeansOfType(AstroExtensionDescriptor.class).values().stream();
        Stream<AstroExtensionDescriptor> spiStream = StreamSupport.stream(ServiceLoader.load(AstroExtensionDescriptor.class).spliterator(), false);

        return Stream.concat(beanStream, spiStream)
                .filter(d -> Objects.nonNull(d) && Objects.nonNull(StringUtils.trimToNull(d.getExtensionKey())))
                .collect(Collectors.toMap(
                        d -> StringUtils.trim(d.getExtensionKey()),
                        d -> d,
                        (existing, replacement) -> existing // 保持 Bean 优先
                ));
    }

    /**
     * ApplicationReadyEvent 触发后执行注册逻辑
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (applicationContext == null || astSystemExtensionMapper == null) {
            log.warn("{} 必要依赖未注入，跳过扩展注册", LOG_PREFIX);
            return;
        }
        delayedRegisterExtensions();
    }

    /**
     * 延迟注册扩展，等待数据库表创建完成
     */
    private void delayedRegisterExtensions() {
        try {
            doRegisterExtensions();
        } catch (Exception e) {
            log.warn("{} 扩展注册失败，将在延迟后重试 | 异常: {}", LOG_PREFIX, e.getMessage());
            try {
                TimeUnit.SECONDS.sleep(2);
                doRegisterExtensions();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("{} 扩展注册重试被中断", LOG_PREFIX);
            } catch (Exception e2) {
                log.error("{} 扩展注册重试失败 | 异常: {}", LOG_PREFIX, e2.getMessage());
            }
        }
    }

    /**
     * 执行扩展注册
     */
    private void doRegisterExtensions() {
        // 注册 Spring Bean 扩展
        registerSpringBeanExtensions();
        // 注册 SPI 扩展
        registerSpiExtensions();
    }

    /**
     * 注册 Spring Bean 扩展
     */
    private void registerSpringBeanExtensions() {
        Map<String, AstroExtensionDescriptor> beanMap = applicationContext.getBeansOfType(AstroExtensionDescriptor.class);
        if (beanMap.isEmpty()) {
            log.info("{} 未发现 Spring Bean 扩展描述符，跳过注册", LOG_PREFIX);
            return;
        }
        beanMap.forEach((key, descriptor) -> processRegistration(
                key,
                descriptor,
                SystemExtensionEnum.DiscoveryMechanismEnum.SPRING_BEAN,
                SystemExtensionEnum.InstallSourceEnum.CLASSPATH_DEPENDENCY));
    }

    /**
     * 注册 SPI 扩展
     */
    private void registerSpiExtensions() {
        Iterable<AstroExtensionDescriptor> spiDescriptors = ServiceLoader.load(AstroExtensionDescriptor.class);
        boolean hasSpiExtensions = false;
        for (AstroExtensionDescriptor descriptor : spiDescriptors) {
            if (descriptor != null && StringUtils.trimToNull(descriptor.getExtensionKey()) != null) {
                hasSpiExtensions = true;
                processRegistration(
                        descriptor.getExtensionKey(),
                        descriptor,
                        SystemExtensionEnum.DiscoveryMechanismEnum.SPI,
                        SystemExtensionEnum.InstallSourceEnum.CLASSPATH_DEPENDENCY);
            }
        }
        if (!hasSpiExtensions) {
            log.info("{} 未发现 SPI 扩展描述符，跳过注册", LOG_PREFIX);
        }
    }

    /**
     * 执行单个描述符的数据库同步
     */
    private void processRegistration(
            String key,
            AstroExtensionDescriptor descriptor,
            SystemExtensionEnum.DiscoveryMechanismEnum discoveryMechanism,
            SystemExtensionEnum.InstallSourceEnum installSource) {
        SystemExtensionEntity entity = buildEntity(descriptor, key, discoveryMechanism, installSource);

        Optional<SystemExtensionEntity> existingOpt = Optional.ofNullable(astSystemExtensionMapper.selectOne(
                new LambdaQueryWrapper<SystemExtensionEntity>()
                        .eq(SystemExtensionEntity::getExtensionKey, key)
                        .last("LIMIT 1")));

        boolean success = existingOpt.map(existing -> {
            entity.setId(existing.getId());
            entity.setApplied(existing.getApplied());
            entity.setStatus(existing.getStatus());
            entity.setJarName(existing.getJarName());
            entity.setExtensionCode(existing.getExtensionCode());
            entity.setInstallSource(Optional.ofNullable(StringUtils.trimToNull(existing.getInstallSource()))
                    .orElse(entity.getInstallSource()));
            entity.setDiscoveryMechanism(Optional.ofNullable(StringUtils.trimToNull(existing.getDiscoveryMechanism()))
                    .orElse(entity.getDiscoveryMechanism()));
            return astSystemExtensionMapper.updateById(entity) > 0;
        }).orElseGet(() -> astSystemExtensionMapper.insert(entity) > 0);

        if (success) {
            log.info("{} 注册成功 | Key: {} | Name: {} | Type: {} | Mechanism: {}",
                    LOG_PREFIX, key, entity.getExtensionName(), entity.getType(), discoveryMechanism.getCode());
        } else {
            log.warn("{} 注册失败 | Key: {}", LOG_PREFIX, key);
        }
    }

    /**
     * 构建系统扩展实体
     */
    private SystemExtensionEntity buildEntity(
            AstroExtensionDescriptor d,
            String key,
            SystemExtensionEnum.DiscoveryMechanismEnum discoveryMechanism,
            SystemExtensionEnum.InstallSourceEnum installSource) {
        SystemExtensionEntity entity = new SystemExtensionEntity();
        entity.setExtensionKey(key);
        entity.setExtensionName(StringUtils.trimToNull(d.getName()));
        entity.setExtensionCode(StringUtils.trimToNull(d.getExtensionCode()));
        entity.setType(Optional.ofNullable(d.getExtensionType()).map(SystemExtensionEnum.ExtensionTypeEnum::getCode).orElse(null));
        entity.setVersion(StringUtils.trimToNull(d.getVersion()));
        entity.setAuthor(StringUtils.trimToNull(d.getAuthor()));
        entity.setDescription(StringUtils.trimToNull(d.getDescription()));
        entity.setAvatar(StringUtils.trimToNull(d.getAvatar()));
        entity.setApplied(SystemExtensionEnum.ApplyStatusEnum.N.getCode());
        entity.setStatus(SystemExtensionEnum.ExtensionInstallStatusEnum.INSTALLED.getCode());
        // 设置发现机制
        entity.setDiscoveryMechanism(discoveryMechanism.getCode());
        entity.setInstallSource(installSource.getCode());
        return entity;
    }
}