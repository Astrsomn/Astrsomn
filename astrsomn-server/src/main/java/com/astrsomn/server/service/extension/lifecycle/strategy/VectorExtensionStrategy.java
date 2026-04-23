package com.astrsomn.server.service.extension.lifecycle.strategy;

import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.constant.SystemExtensionEnum;
import com.astrsomn.core.common.entity.SystemExtensionEntity;
import com.astrsomn.core.common.utils.StringUtils;
import com.astrsomn.core.exception.base.BusinessException;
import com.astrsomn.core.exception.constant.SystemExtensionErrorEnum;
import com.astrsomn.server.service.extension.base.SystemExtensionVecDriverSyncService;
import com.astrsomn.server.service.extension.guard.SystemExtensionVecGuard;
import com.astrsomn.server.service.extension.lifecycle.ExtensionLifecycleStrategy;
import com.astrsomn.server.service.extension.support.SystemExtensionSourceHelper;
import com.astrsomn.server.service.extension.warmup.VectorSourceInitializer;
import com.astrsomn.starter.plugin.AstrsomnPluginManager;
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
            if (jarName != null && SystemExtensionSourceHelper.isPluginJarSource(extension)) {
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
        if (jarName != null && SystemExtensionSourceHelper.isPluginJarSource(extension)) {
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
        if (jarName != null && SystemExtensionSourceHelper.isPluginJarSource(extension)) {
            pluginManager.unloadPlugin(jarName);
        }
    }
}

