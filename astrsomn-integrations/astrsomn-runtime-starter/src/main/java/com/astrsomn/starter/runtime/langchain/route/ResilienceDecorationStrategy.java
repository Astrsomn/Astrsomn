package com.astrsomn.starter.runtime.langchain.route;

import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelRouteSetting;

import java.util.function.Supplier;

/**
 * 对单次模型调用套 Resilience4j 等装饰；跨节点故障转移由组合模型自行循环，本策略只做「单端点单次调用」包装。
 */
public interface ResilienceDecorationStrategy {

    String getId();

    <T> T decorate(Supplier<T> supplier, ModelRouteContext ctx, int endpointIndex, ModelRouteSetting route);
}
