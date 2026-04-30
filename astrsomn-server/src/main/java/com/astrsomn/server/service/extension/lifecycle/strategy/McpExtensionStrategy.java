package com.astrsomn.server.service.extension.lifecycle.strategy;

import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.constant.SystemExtensionEnum;
import com.astrsomn.core.common.entity.SystemExtensionEntity;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.core.exception.SystemExtensionErrorEnum;
import com.astrsomn.server.service.extension.lifecycle.ExtensionLifecycleStrategy;
import com.astrsomn.server.service.extension.support.SystemExtensionSourceHelper;
import com.astrsomn.starter.plugin.AstrsomnPluginManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class McpExtensionStrategy implements ExtensionLifecycleStrategy {

    private final AstrsomnPluginManager pluginManager;

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum supportType() {
        return SystemExtensionEnum.ExtensionTypeEnum.MCP;
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
        String jarName = StringUtils.trimToNull(extension.getJarName());
        if (jarName != null && SystemExtensionSourceHelper.isPluginJarSource(extension)) {
            pluginManager.unloadPlugin(jarName);
        }
    }

    @Override
    public void uninstall(SystemExtensionEntity extension) {
        String jarName = StringUtils.trimToNull(extension.getJarName());
        if (jarName != null && SystemExtensionSourceHelper.isPluginJarSource(extension)) {
            pluginManager.unloadPlugin(jarName);
        }
    }
}

