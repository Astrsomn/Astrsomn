package org.astrsomn.server.service.extension.dependency;

import org.astrsomn.core.common.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 从扩展描述文案解析依赖声明。
 * 支持格式:
 * deps:hard=extA,extB;soft=extC
 */
@Component
public class ExtensionDependencyParser {

    private static final String PREFIX = "deps:";

    public ExtensionDependencyParseResult parse(String rawDescription) {
        String text = StringUtils.trimToNull(rawDescription);
        if (text == null) {
            return ExtensionDependencyParseResult.empty();
        }
        int start = text.toLowerCase().indexOf(PREFIX);
        if (start < 0) {
            return ExtensionDependencyParseResult.empty();
        }
        String payload = text.substring(start + PREFIX.length()).trim();
        if (payload.isEmpty()) {
            return ExtensionDependencyParseResult.empty();
        }

        List<ExtensionDependencySpec> deps = new ArrayList<>();

        String[] groups = payload.split(";");
        for (String group : groups) {
            String g = StringUtils.trimToNull(group);
            if (g == null) {
                continue;
            }
            String[] pair = g.split("=", 2);
            if (pair.length != 2) {
                continue;
            }
            String scopeRaw = StringUtils.trimToNull(pair[0]);
            String keysRaw = StringUtils.trimToNull(pair[1]);
            if (scopeRaw == null || keysRaw == null) {
                continue;
            }
            ExtensionDependencyScope scope = "hard".equalsIgnoreCase(scopeRaw)
                    ? ExtensionDependencyScope.HARD
                    : "soft".equalsIgnoreCase(scopeRaw) ? ExtensionDependencyScope.SOFT : null;
            if (scope == null) {
                continue;
            }
            for (String key : keysRaw.split(",")) {
                String depKey = StringUtils.trimToNull(key);
                if (depKey != null) {
                    deps.add(new ExtensionDependencySpec(depKey, scope));
                }
            }
        }
        return new ExtensionDependencyParseResult(deps);
    }
}

