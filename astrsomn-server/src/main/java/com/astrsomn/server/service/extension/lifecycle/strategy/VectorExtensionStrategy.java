package com.astrsomn.server.service.extension.lifecycle.strategy;

import com.astrsomn.api.runtime.common.constant.SystemExtensionEnum;
import com.astrsomn.api.runtime.common.entity.SystemExtensionEntity;
import com.astrsomn.api.runtime.exception.SystemExtensionErrorEnum;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.service.extension.guard.SystemExtensionVecGuard;
import com.astrsomn.server.service.extension.lifecycle.ExtensionLifecycleStrategy;
import com.astrsomn.server.service.extension.support.SystemExtensionSourceHelper;
import com.astrsomn.server.service.extension.warmup.VectorSourceInitializer;
import com.astrsomn.starter.runtime.plugin.AstrsomnPluginManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VectorExtensionStrategy implements ExtensionLifecycleStrategy {

    private final AstrsomnPluginManager pluginManager;
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
    }

    @Override
    public void uninstall(SystemExtensionEntity extension) {
        BaseResponse<Void> guard = vecGuard.assertNoVecSourcesUseProvider(extension.getId());
        if (!guard.isSuccess()) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PERMISSION_DENIED, guard.getMessage());
        }
        String jarName = StringUtils.trimToNull(extension.getJarName());
        if (jarName != null && SystemExtensionSourceHelper.isPluginJarSource(extension)) {
            pluginManager.unloadPlugin(jarName);
        }
    }
}

