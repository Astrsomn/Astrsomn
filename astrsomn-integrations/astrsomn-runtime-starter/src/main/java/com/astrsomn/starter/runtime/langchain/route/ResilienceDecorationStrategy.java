package com.astrsomn.starter.runtime.langchain.route;

import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelRouteSetting;

import java.util.function.Supplier;


public interface ResilienceDecorationStrategy {

    String getId();

    <T> T decorate(Supplier<T> supplier, ModelRouteContext ctx, int endpointIndex, ModelRouteSetting route);
}
