package com.astrsomn.starter.runtime.langchain.exception;


public class InstanceNotFoundException extends AstroConfigException {

    public InstanceNotFoundException(String instanceKey, String envCode) {
        super(ErrorCode.INSTANCE_NOT_FOUND, "instanceKey=" + instanceKey + ", envCode=" + envCode,
                context().put("instanceKey", instanceKey).put("envCode", envCode).build());
    }

    
    public InstanceNotFoundException(String agentKey, String envCode, boolean forAgent) {
        super(ErrorCode.INSTANCE_NOT_FOUND, "agentKey=" + agentKey + " has no available instances, envCode=" + envCode,
                context().put("agentKey", agentKey).put("envCode", envCode).build());
    }
}
