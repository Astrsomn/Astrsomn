package com.astrsomn.server.service.system.extension.lifecycle.strategy;

import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.service.system.extension.guard.SystemExtensionModelGuard;
import com.astrsomn.server.service.system.extension.lifecycle.ExtensionLifecycleStrategy;
import com.astrsomn.server.service.system.extension.support.SystemExtensionSourceHelper;
import com.astrsomn.starter.runtime.plugin.AstrsomnPluginManager;
import com.astrsomn.system.constant.SystemExtensionEnum;
import com.astrsomn.system.entity.SystemExtensionEntity;
import com.astrsomn.system.exception.SystemExtensionErrorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelExtensionStrategy implements ExtensionLifecycleStrategy {

    private final AstrsomnPluginManager pluginManager;
    private final SystemExtensionModelGuard modelGuard;

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum supportType() {
        return SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER;
    }

    @Override
    public void apply(SystemExtensionEntity extension) {
        if (!SystemExtensionSourceHelper.isPluginJarSource(extension)) {
            return;
        }
        String jarName = StringUtils.trimToNull(extension.getJarName());
        if (jarName == null) {
            return;
        }
        try {
            pluginManager.applyPlugin(jarName);
        } catch (Exception e) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_APPLY_FAILED, e.getMessage());
        }
    }

    @Override
    public void revoke(SystemExtensionEntity extension) {
        BaseResponse<Void> guard = modelGuard.assertNoInstancesUseProviderModels(extension.getId());
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
        BaseResponse<Void> guard = modelGuard.assertNoAiModelsForProviderExtension(extension.getId());
        if (!guard.isSuccess()) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PERMISSION_DENIED, guard.getMessage());
        }
        String jarName = StringUtils.trimToNull(extension.getJarName());
        if (jarName != null && SystemExtensionSourceHelper.isPluginJarSource(extension)) {
            pluginManager.unloadPlugin(jarName);
        }
    }
}

