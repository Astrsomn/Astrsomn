package org.astrsomn.server.service.extension.lifecycle.strategy;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.SystemExtensionErrorEnum;
import org.astrsomn.server.service.extension.lifecycle.ExtensionLifecycleStrategy;
import org.astrsomn.starter.plugin.AstrsomnPluginManager;
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
        if (jarName != null) {
            pluginManager.unloadPlugin(jarName);
        }
    }

    @Override
    public void uninstall(SystemExtensionEntity extension) {
        String jarName = StringUtils.trimToNull(extension.getJarName());
        if (jarName != null) {
            pluginManager.unloadPlugin(jarName);
        }
    }
}

