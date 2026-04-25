package com.astrsomn.server.plugin.registry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.core.common.constant.SystemExtensionEnum;
import com.astrsomn.core.common.dto.extension.SystemExtensionMetaData;
import com.astrsomn.core.common.entity.SystemExtensionEntity;
import com.astrsomn.core.common.utils.StringUtils;
import com.astrsomn.starter.mapper.SystemExtensionMapper;
import com.astrsomn.server.plugin.metadata.ExtensionJarMetadataReader;
import com.astrsomn.starter.plugin.AstrsomnPluginManager;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PluginDirectoryExtensionSyncService {

    private static final String LOG_PREFIX = "[Astrsomn] [插件目录同步] ====> ";

    private final AstrsomnPluginManager pluginManager;
    private final SystemExtensionMapper systemExtensionMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void syncOnApplicationReady() {
        syncDiscoveredPlugins();
    }

    public void syncDiscoveredPlugins() {
        File dir = pluginManager.getPluginsDirectory();
        File[] jarFiles = dir.listFiles((ignored, name) -> name.endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            log.debug("{} plugins 目录为空，跳过同步", LOG_PREFIX);
            return;
        }
        Arrays.stream(jarFiles).forEach(this::syncSingleJar);
    }

    private void syncSingleJar(File jar) {
        Optional<SystemExtensionMetaData> metaOpt = ExtensionJarMetadataReader.tryLoad(jar);
        if (metaOpt.isEmpty()) {
            log.debug("{} 跳过未识别扩展元数据的 jar: {}", LOG_PREFIX, jar.getName());
            return;
        }
        SystemExtensionMetaData meta = metaOpt.get();
        String key = StringUtils.trimToNull(meta.extensionKey());
        if (key == null) {
            log.warn("{} 扩展缺少 extensionKey，跳过登记: {}", LOG_PREFIX, jar.getName());
            return;
        }

        SystemExtensionEntity entity = buildEntity(meta, jar.getName());
        Optional<SystemExtensionEntity> existingOpt = Optional.ofNullable(systemExtensionMapper.selectOne(
                new LambdaQueryWrapper<SystemExtensionEntity>()
                        .eq(SystemExtensionEntity::getExtensionKey, key)
                        .last("LIMIT 1")));

        boolean success = existingOpt.map(existing -> {
            entity.setId(existing.getId());
            entity.setApplied(existing.getApplied());
            entity.setStatus(existing.getStatus());
            entity.setJarName(Optional.ofNullable(StringUtils.trimToNull(existing.getJarName())).orElse(entity.getJarName()));
            entity.setProviderCode(Optional.ofNullable(StringUtils.trimToNull(existing.getProviderCode())).orElse(entity.getProviderCode()));
            entity.setInstallSource(Optional.ofNullable(StringUtils.trimToNull(existing.getInstallSource())).orElse(entity.getInstallSource()));
            entity.setDiscoveryMechanism(Optional.ofNullable(StringUtils.trimToNull(existing.getDiscoveryMechanism()))
                    .orElse(entity.getDiscoveryMechanism()));
            return systemExtensionMapper.updateById(entity) > 0;
        }).orElseGet(() -> systemExtensionMapper.insert(entity) > 0);

        if (success) {
            log.info("{} 同步插件目录扩展成功 | Key: {} | Jar: {}", LOG_PREFIX, key, jar.getName());
        } else {
            log.warn("{} 同步插件目录扩展失败 | Key: {} | Jar: {}", LOG_PREFIX, key, jar.getName());
        }
    }

    private static SystemExtensionEntity buildEntity(SystemExtensionMetaData meta, String jarName) {
        SystemExtensionEntity entity = new SystemExtensionEntity();
        entity.setExtensionKey(StringUtils.trimToNull(meta.extensionKey()));
        entity.setExtensionName(StringUtils.trimToNull(meta.extensionName()));
        entity.setType(StringUtils.trimToNull(meta.type()));
        entity.setVersion(StringUtils.trimToNull(meta.version()));
        entity.setAuthor(StringUtils.trimToNull(meta.author()));
        entity.setDescription(StringUtils.trimToNull(meta.description()));
        entity.setAvatar(StringUtils.trimToNull(meta.avatar()));
        entity.setProviderCode(StringUtils.trimToNull(meta.providerCode()));
        entity.setJarName(StringUtils.trimToNull(jarName));
        entity.setApplied(SystemExtensionEnum.ApplyStatusEnum.N.getCode());
        entity.setStatus(SystemExtensionEnum.ExtensionInstallStatusEnum.INSTALLED.getCode());
        entity.setDiscoveryMechanism(SystemExtensionEnum.DiscoveryMechanismEnum.SPI.getCode());
        entity.setInstallSource(SystemExtensionEnum.InstallSourceEnum.PLUGIN_JAR_DISCOVERED.getCode());
        return entity;
    }
}
