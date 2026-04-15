package org.astrsomn.server.plugin.registry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.mapper.SystemExtensionMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemExtensionRegistry {

    private final ApplicationContext applicationContext;
    private final SystemExtensionMapper systemExtensionMapper;

    /**
     * Spring Bean 优先，SPI 仅补充尚未出现的 extensionKey。市场目录、头像补全等可与注册共用同一套合并结果。
     */
    public static Map<String, AstroExtensionDescriptor> mergeDescriptors(ApplicationContext applicationContext) {
        Map<String, AstroExtensionDescriptor> mergedByKey = new HashMap<>();
        applicationContext.getBeansOfType(AstroExtensionDescriptor.class).forEach((beanName, descriptor) -> {
            String extensionKey = StringUtils.trimToNull(descriptor.getExtensionKey());
            if (extensionKey != null) {
                mergedByKey.putIfAbsent(extensionKey, descriptor);
            }
        });
        ServiceLoader<AstroExtensionDescriptor> serviceLoader =
                ServiceLoader.load(AstroExtensionDescriptor.class);
        for (AstroExtensionDescriptor descriptor : serviceLoader) {
            if (descriptor == null) {
                continue;
            }
            String extensionKey = StringUtils.trimToNull(descriptor.getExtensionKey());
            if (extensionKey == null) {
                continue;
            }
            mergedByKey.putIfAbsent(extensionKey, descriptor);
        }
        return mergedByKey;
    }

    @PostConstruct
    public void registerExtensions() {
        Map<String, AstroExtensionDescriptor> mergedByKey = mergeDescriptors(applicationContext);

        if (mergedByKey.isEmpty()) {
            log.info("No AstroExtensionDescriptor found via Spring or SPI, skip system extension registration.");
            return;
        }

        mergedByKey.forEach(this::registerDescriptor);
    }

    private void registerDescriptor(String beanName, AstroExtensionDescriptor descriptor) {
        String extensionKey = StringUtils.trimToNull(descriptor.getExtensionKey());
        if (extensionKey == null) {
            log.warn("Skip registering extension bean {} because extensionKey is blank.", beanName);
            return;
        }

        SystemExtensionEntity existing = systemExtensionMapper.selectOne(
                new LambdaQueryWrapper<SystemExtensionEntity>()
                        .eq(SystemExtensionEntity::getExtensionKey, extensionKey)
                        .last("LIMIT 1"));

        SystemExtensionEntity entity = buildEntity(descriptor, extensionKey);
        boolean created;
        if (existing == null) {
            created = systemExtensionMapper.insert(entity) > 0;
        } else {
            entity.setId(existing.getId());
            // 仅同步 SPI 元数据，保留用户操作状态（已应用 / 插件 jar 名等），避免每次启动把「已应用」写回未应用
            entity.setApplied(existing.getApplied());
            entity.setStatus(existing.getStatus());
            entity.setJarName(existing.getJarName());
            entity.setProviderCode(existing.getProviderCode());
            created = systemExtensionMapper.updateById(entity) > 0;
        }

        if (created) {
            log.info("Registered extension: key={}, name={}, type={}",
                    entity.getExtensionKey(), entity.getExtensionName(), entity.getType());
        } else {
            log.warn("Failed to register extension: key={}", extensionKey);
        }
    }

    private SystemExtensionEntity buildEntity(AstroExtensionDescriptor descriptor, String extensionKey) {
        SystemExtensionEntity entity = new SystemExtensionEntity();
        entity.setExtensionKey(extensionKey);
        entity.setExtensionName(StringUtils.trimToNull(descriptor.getName()));
        entity.setType(descriptor.getExtensionType() == null ? null : descriptor.getExtensionType().getCode());
        entity.setVersion(StringUtils.trimToNull(descriptor.getVersion()));
        entity.setAuthor(StringUtils.trimToNull(descriptor.getAuthor()));
        entity.setDescription(StringUtils.trimToNull(descriptor.getDescription()));
        entity.setAvatar(StringUtils.trimToNull(descriptor.getAvatar()));
        entity.setApplied(SystemExtensionEnum.ApplyStatusEnum.N.getCode());
        entity.setStatus(SystemExtensionEnum.ExtensionInstallStatusEnum.INSTALLED.getCode());
        return entity;
    }
}

