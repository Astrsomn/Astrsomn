package com.astrsomn.api.runtime.common.langchain.buildParam.setting;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 多节点路由与故障转移配置；未启用时行为与仅使用 {@link ModelSetting} 单组连接一致。
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ModelRouteSetting {

    private boolean enabled = false;

    /**
     * 内置：roundRobin、random、weightedRandom、stickyMemory、failoverOrdered；
     * 也可为 Spring 中注册的自定义策略 bean 名。
     */
    private String selectionStrategyId = "roundRobin";

    /**
     * 跨节点最大尝试次数（含首次），默认 2 表示最多试两个节点。
     */
    private int failoverMaxAttempts = 2;

    private List<ModelEndpoint> endpoints = new ArrayList<>();

    /**
     * 韧性装饰策略：noop、registry（使用 Resilience4j 注册表与 {@link #resilienceInstanceNameTemplate}）。
     */
    private String resilienceStrategyId = "noop";

    /**
     * 与 resilience4j.instances 下的实例名拼接或替换；支持占位符 {bizKey}、{instanceKey}、{modelKey}、{endpointIndex}。
     */
    private String resilienceInstanceNameTemplate = "astroModel-{bizKey}";

    public static ModelRouteSetting disabled() {
        ModelRouteSetting s = new ModelRouteSetting();
        s.setEnabled(false);
        return s;
    }
}
