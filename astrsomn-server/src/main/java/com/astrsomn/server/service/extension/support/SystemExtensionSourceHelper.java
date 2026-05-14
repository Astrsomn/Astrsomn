package com.astrsomn.server.service.extension.support;

import com.astrsomn.api.runtime.common.constant.SystemExtensionEnum;
import com.astrsomn.api.runtime.common.entity.SystemExtensionEntity;
import com.astrsomn.common.utils.StringUtils;

public final class SystemExtensionSourceHelper {

    private SystemExtensionSourceHelper() {
    }

    public static String resolveInstallSource(SystemExtensionEntity extension) {
        if (extension == null) {
            return null;
        }
        String explicit = StringUtils.trimToNull(extension.getInstallSource());
        if (explicit != null) {
            return explicit;
        }
        // Backward compatibility for historical rows before INSTALL_SOURCE existed.
        return StringUtils.trimToNull(extension.getJarName()) != null
                ? SystemExtensionEnum.InstallSourceEnum.PLUGIN_JAR_UPLOAD.getCode()
                : SystemExtensionEnum.InstallSourceEnum.CLASSPATH_DEPENDENCY.getCode();
    }

    public static boolean isPluginJarSource(SystemExtensionEntity extension) {
        String source = resolveInstallSource(extension);
        return SystemExtensionEnum.InstallSourceEnum.PLUGIN_JAR_UPLOAD.getCode().equals(source)
                || SystemExtensionEnum.InstallSourceEnum.PLUGIN_JAR_DISCOVERED.getCode().equals(source);
    }

    public static boolean canDeleteJarFromDisk(SystemExtensionEntity extension) {
        return SystemExtensionEnum.InstallSourceEnum.PLUGIN_JAR_UPLOAD.getCode().equals(resolveInstallSource(extension));
    }

    public static boolean canUninstall(SystemExtensionEntity extension) {
        return isPluginJarSource(extension);
    }
}
