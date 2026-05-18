package com.astrsomn.starter.runtime.langchain.route;

import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelEndpoint;

import java.util.List;


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
