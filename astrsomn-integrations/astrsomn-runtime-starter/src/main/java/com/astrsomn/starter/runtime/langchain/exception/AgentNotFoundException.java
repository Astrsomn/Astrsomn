package com.astrsomn.starter.runtime.langchain.exception;

/**
 * 按 agentKey 未找到智能体配置。
 */
public class AgentNotFoundException extends AstroConfigException {

    public AgentNotFoundException(String agentKey, String envCode) {
        super(ErrorCode.AGENT_NOT_FOUND, "agentKey=" + agentKey + ", envCode=" + envCode,
                context().put("agentKey", agentKey).put("envCode", envCode).build());
    }
}
