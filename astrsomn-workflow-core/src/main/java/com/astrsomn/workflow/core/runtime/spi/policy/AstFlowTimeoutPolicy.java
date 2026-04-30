package com.astrsomn.workflow.core.runtime.spi.policy;

public interface AstFlowTimeoutPolicy {

    long timeoutMs(String nodeType);
}
