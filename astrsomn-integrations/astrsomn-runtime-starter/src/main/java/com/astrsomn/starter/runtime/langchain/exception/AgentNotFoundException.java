package com.astrsomn.starter.runtime.langchain.exception;


public class AgentNotFoundException extends AstroConfigException {

    public AgentNotFoundException(String agentKey, String envCode) {
        super(ErrorCode.AGENT_NOT_FOUND, "agentKey=" + agentKey + ", envCode=" + envCode,
                context().put("agentKey", agentKey).put("envCode", envCode).build());
    }
}
