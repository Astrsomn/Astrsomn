package com.astrsomn.starter.runtime.langchain.route;

import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelEndpoint;

import java.util.List;

/**
 * 一次路由决策的输入：会话维度、故障转移尝试序号、调用单调计数、端点列表。
 */
public record ModelRouteSelectionInput(
        ModelRouteContext context,
        int attemptIndex,
        int monotonicCallNumber,
        List<ModelEndpoint> endpoints
) {
    public int size() {
        return endpoints == null ? 0 : endpoints.size();
    }
}
