package com.astrsomn.starter.runtime.langchain.route.resilience;

import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelRouteSetting;
import com.astrsomn.starter.runtime.langchain.route.ModelRouteContext;

public final class ResilienceInstanceNames {

    private ResilienceInstanceNames() {
    }

    public static String resolve(ModelRouteSetting route, ModelRouteContext ctx, int endpointIndex) {
        String t = route.getResilienceInstanceNameTemplate();
        if (t == null || t.isBlank()) {
            t = "astroModel-{bizKey}";
        }
        return t.replace("{bizKey}", nz(ctx.bizKey()))
                .replace("{instanceKey}", nz(ctx.instanceKey()))
                .replace("{modelKey}", nz(ctx.modelKey()))
                .replace("{endpointIndex}", String.valueOf(endpointIndex));
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
