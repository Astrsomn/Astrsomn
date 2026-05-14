package com.astrsomn.api.workflow.runtime.spi.policy;

public interface AstFlowRetryPolicy {

    boolean shouldRetry(String nodeType, int attemptNo, Throwable throwable);
}