package com.astrsomn.server.service.extension.dependency;

public record ExtensionDependencySpec(
        String dependencyKey,
        ExtensionDependencyScope scope) {
}

