package org.astrsomn.server.plugin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;
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

    @PostConstruct
    public void registerExtensions() {
        // 1) 先从 Spring 容器拿到（兼容历史实现）
        // 2) 再从 Java SPI（ServiceLoader）加载（让插件扩展元数据摆脱 Spring 注册）
        Map<String, AstroExtensionDescriptor> mergedByKey = new HashMap<>();

        Map<String, AstroExtensionDescriptor> springDescriptors =
                applicationContext.getBeansOfType(AstroExtensionDescriptor.class);
        springDescriptors.forEach((beanName, descriptor) -> {
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

        if (mergedByKey.isEmpty()) {
            log.info("No AstroExtensionDescriptor found via Spring or SPI, skip system extension registration.");
            return;
        }

        mergedByKey.forEach((extensionKey, descriptor) -> registerDescriptor(extensionKey, descriptor));
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
                        .last("FETCH FIRST 1 ROWS ONLY"));

        SystemExtensionEntity entity = buildEntity(descriptor, extensionKey);
        boolean created;
        if (existing == null) {
            created = systemExtensionMapper.insert(entity) > 0;
        } else {
            entity.setId(existing.getId());
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
        entity.setApplied("Y");
        entity.setStatus("APPLIED");
        return entity;
    }
}
