package com.astrsomn.starter.runtime.langchain.route;

import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;

/**
 * 轻量路由上下文，不持有完整 {@link AstroChatParam}，避免策略层与业务参数强耦合。
 */
public record ModelRouteContext(
        String memoryKey,
        String agentKey,
        String instanceKey,
        String modelKey
) {
    public static ModelRouteContext from(AstroChatParam<?> param) {
        if (param == null) {
            return new ModelRouteContext(null, null, null, null);
        }
        return new ModelRouteContext(
                param.getMemoryKey(),
                param.getAgentKey(),
                param.getInstanceKey(),
                param.getModelKey());
    }
}
