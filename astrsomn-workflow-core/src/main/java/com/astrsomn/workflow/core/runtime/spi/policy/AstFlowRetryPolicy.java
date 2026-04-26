package com.astrsomn.workflow.core.runtime.spi.policy;

public interface AstFlowRetryPolicy {

    boolean shouldRetry(String nodeType, int attemptNo, Throwable throwable);
}
