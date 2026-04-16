package org.astrsomn.server.plugin.registry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.core.mapper.SystemExtensionMapper;
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
        beanMap.forEach((key, descriptor) -> processRegistration(key, descriptor, SystemExtensionEnum.DiscoveryMechanismEnum.SPI));
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
                processRegistration(descriptor.getExtensionKey(), descriptor, SystemExtensionEnum.DiscoveryMechanismEnum.SPI);
            }
        }
        if (!hasSpiExtensions) {
            log.info("{} 未发现 SPI 扩展描述符，跳过注册", LOG_PREFIX);
        }
    }

    /**
     * 执行单个描述符的数据库同步
     */
    private void processRegistration(String key, AstroExtensionDescriptor descriptor, SystemExtensionEnum.DiscoveryMechanismEnum discoveryMechanism) {
        SystemExtensionEntity entity = buildEntity(descriptor, key, discoveryMechanism);

        Optional<SystemExtensionEntity> existingOpt = Optional.ofNullable(systemExtensionMapper.selectOne(
                new LambdaQueryWrapper<SystemExtensionEntity>()
                        .eq(SystemExtensionEntity::getExtensionKey, key)
                        .last("LIMIT 1")));

        boolean success = existingOpt.map(existing -> {
            entity.setId(existing.getId());
            entity.setApplied(existing.getApplied());
            entity.setStatus(existing.getStatus());
            entity.setJarName(existing.getJarName());
            entity.setProviderCode(existing.getProviderCode());
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
    private SystemExtensionEntity buildEntity(AstroExtensionDescriptor d, String key, SystemExtensionEnum.DiscoveryMechanismEnum discoveryMechanism) {
        SystemExtensionEntity entity = new SystemExtensionEntity();
        entity.setExtensionKey(key);
        entity.setExtensionName(StringUtils.trimToNull(d.getName()));
        entity.setType(Optional.ofNullable(d.getExtensionType()).map(SystemExtensionEnum.ExtensionTypeEnum::getCode).orElse(null));
        entity.setVersion(StringUtils.trimToNull(d.getVersion()));
        entity.setAuthor(StringUtils.trimToNull(d.getAuthor()));
        entity.setDescription(StringUtils.trimToNull(d.getDescription()));
        entity.setAvatar(StringUtils.trimToNull(d.getAvatar()));
        entity.setApplied(SystemExtensionEnum.ApplyStatusEnum.N.getCode());
        entity.setStatus(SystemExtensionEnum.ExtensionInstallStatusEnum.INSTALLED.getCode());
        // 设置发现机制
        entity.setDiscoveryMechanism(discoveryMechanism.getCode());
        return entity;
    }
}