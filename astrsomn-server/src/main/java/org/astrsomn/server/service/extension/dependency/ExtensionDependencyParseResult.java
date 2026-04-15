package org.astrsomn.server.service.extension.dependency;

import org.astrsomn.core.common.constant.SystemExtensionEnum;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public record ExtensionDependencyParseResult(
        List<ExtensionDependencySpec> dependencies,
        Set<SystemExtensionEnum.DiscoveryMechanismEnum> mechanisms) {

    public static ExtensionDependencyParseResult empty() {
        return new ExtensionDependencyParseResult(List.of(), Set.of(SystemExtensionEnum.DiscoveryMechanismEnum.SPI));
    }

    public List<ExtensionDependencySpec> safeDependencies() {
        return dependencies == null ? List.of() : Collections.unmodifiableList(dependencies);
    }
}

