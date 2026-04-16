package org.astrsomn.server.service.extension.lifecycle.strategy;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.SystemExtensionErrorEnum;
import org.astrsomn.server.service.SystemExtensionVecDriverSyncService;
import org.astrsomn.server.service.extension.guard.SystemExtensionVecGuard;
import org.astrsomn.server.service.extension.lifecycle.ExtensionLifecycleStrategy;
import org.astrsomn.server.service.extension.warmup.VectorSourceInitializer;
import org.astrsomn.starter.plugin.AstrsomnPluginManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VectorExtensionStrategy implements ExtensionLifecycleStrategy {

    private final AstrsomnPluginManager pluginManager;
    private final SystemExtensionVecDriverSyncService vecDriverSyncService;
    private final SystemExtensionVecGuard vecGuard;
    private final VectorSourceInitializer vectorProviderWarmupService;

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum supportType() {
        return SystemExtensionEnum.ExtensionTypeEnum.VECTOR_STORE;
    }

    @Override
    public void apply(SystemExtensionEntity extension) {
        String jarName = StringUtils.trimToNull(extension.getJarName());
        try {
            if (jarName != null) {
                pluginManager.applyPlugin(jarName);
            }
            vecDriverSyncService.upsertFromExtension(extension);
            vectorProviderWarmupService.warmupEnabledSourcesByProvider(extension.getExtensionKey());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_APPLY_FAILED, e.getMessage());
        }
    }

    @Override
    public void revoke(SystemExtensionEntity extension) {
        BaseResponse<Void> guard = vecGuard.assertNoVecSourcesUseProvider(extension.getId());
        if (!guard.isSuccess()) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PERMISSION_DENIED, guard.getMessage());
        }
        String jarName = StringUtils.trimToNull(extension.getJarName());
        if (jarName != null) {
            pluginManager.unloadPlugin(jarName);
        }
        String provider = StringUtils.trimToNull(extension.getExtensionKey());
        if (provider != null) {
            vecDriverSyncService.removeDriverRowForProvider(provider);
        }
    }

    @Override
    public void uninstall(SystemExtensionEntity extension) {
        BaseResponse<Void> guard = vecGuard.assertNoVecSourcesUseProvider(extension.getId());
        if (!guard.isSuccess()) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PERMISSION_DENIED, guard.getMessage());
        }
        String provider = StringUtils.trimToNull(extension.getExtensionKey());
        if (provider != null) {
            vecDriverSyncService.removeDriverRowForProvider(provider);
        }
        String jarName = StringUtils.trimToNull(extension.getJarName());
        if (jarName != null) {
            pluginManager.unloadPlugin(jarName);
        }
    }
}

