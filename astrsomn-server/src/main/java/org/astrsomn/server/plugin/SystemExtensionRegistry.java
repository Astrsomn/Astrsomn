package org.astrsomn.server.plugin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;
import org.astrsomn.core.mapper.SystemExtensionMapper;
import org.astrsomn.server.plugin.support.AstroExtensionDescriptorMerge;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemExtensionRegistry {

    private final ApplicationContext applicationContext;
    private final SystemExtensionMapper systemExtensionMapper;

    @PostConstruct
    public void registerExtensions() {
        Map<String, AstroExtensionDescriptor> mergedByKey =
                AstroExtensionDescriptorMerge.merge(applicationContext);

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
        entity.setStatus(SystemExtensionEnum.StatusEnum.DISABLED.getCode());
        return entity;
    }
}
