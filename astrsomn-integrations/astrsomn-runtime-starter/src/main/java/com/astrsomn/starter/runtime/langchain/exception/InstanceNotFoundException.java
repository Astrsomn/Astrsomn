package com.astrsomn.starter.runtime.langchain.exception;

/**
 * 按 instanceKey 未找到推理实例，或 Agent 未关联任何可用实例。
 */
public class InstanceNotFoundException extends AstroConfigException {

    public InstanceNotFoundException(String instanceKey, String envCode) {
        super(ErrorCode.INSTANCE_NOT_FOUND, "instanceKey=" + instanceKey + ", envCode=" + envCode,
                context().put("instanceKey", instanceKey).put("envCode", envCode).build());
    }

    /**
     * Agent 未关联任何可用推理实例。
     */
    public InstanceNotFoundException(String agentKey, String envCode, boolean forAgent) {
        super(ErrorCode.INSTANCE_NOT_FOUND, "agentKey=" + agentKey + " has no available instances, envCode=" + envCode,
                context().put("agentKey", agentKey).put("envCode", envCode).build());
    }
}
