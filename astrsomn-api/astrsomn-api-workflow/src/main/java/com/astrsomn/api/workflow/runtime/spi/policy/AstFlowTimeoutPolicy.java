package com.astrsomn.api.workflow.runtime.spi.policy;

public interface AstFlowTimeoutPolicy {

    long timeoutMs(String nodeType);
}
