package org.astrsomn.server.service.extension.dependency;

import org.astrsomn.core.common.constant.SystemExtensionEnum;

public record ExtensionDependencySpec(
        String dependencyKey,
        SystemExtensionEnum.DiscoveryMechanismEnum scope) {
}

