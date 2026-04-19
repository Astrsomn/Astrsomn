package org.astrsomn.server.service.extension.dependency;

import java.util.Collections;
import java.util.List;

public record ExtensionDependencyParseResult(List<ExtensionDependencySpec> dependencies) {

    public static ExtensionDependencyParseResult empty() {
        return new ExtensionDependencyParseResult(List.of());
    }

    public List<ExtensionDependencySpec> safeDependencies() {
        return dependencies == null ? List.of() : Collections.unmodifiableList(dependencies);
    }
}

