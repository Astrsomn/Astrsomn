package com.astrsomn.server.service.extension.lifecycle.strategy;

import com.astrsomn.system.constant.SystemExtensionEnum;
import com.astrsomn.system.entity.SystemExtensionEntity;
import com.astrsomn.system.exception.SystemExtensionErrorEnum;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.service.extension.lifecycle.ExtensionLifecycleStrategy;
import com.astrsomn.server.service.extension.support.SystemExtensionSourceHelper;
import com.astrsomn.starter.runtime.plugin.AstrsomnPluginManager;
import lombok.RequiredArgsConstructor;
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

