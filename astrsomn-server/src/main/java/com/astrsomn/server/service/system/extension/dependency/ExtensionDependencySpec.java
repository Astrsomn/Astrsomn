package com.astrsomn.server.service.system.extension.dependency;

public record ExtensionDependencySpec(
        String dependencyKey,
        ExtensionDependencyScope scope) {
}

