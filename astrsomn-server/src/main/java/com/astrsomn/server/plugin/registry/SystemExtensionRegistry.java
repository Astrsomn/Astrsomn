package com.astrsomn.server.plugin.registry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.core.common.constant.SystemExtensionEnum;
import com.astrsomn.core.common.entity.SystemExtensionEntity;
import com.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.starter.mapper.SystemExtensionMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemExtensionRegistry {

    private static final String LOG_PREFIX = "[Astrsomn] [扩展注册中心] ====> ";

    private final ApplicationContext applicationContext;
    private final SystemExtensionMapper systemExtensionMapper;

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
     * 初始化注册逻辑
     */
    @PostConstruct
    public void registerExtensions() {
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

        Optional<SystemExtensionEntity> existingOpt = Optional.ofNullable(systemExtensionMapper.selectOne(
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
            return systemExtensionMapper.updateById(entity) > 0;
        }).orElseGet(() -> systemExtensionMapper.insert(entity) > 0);

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