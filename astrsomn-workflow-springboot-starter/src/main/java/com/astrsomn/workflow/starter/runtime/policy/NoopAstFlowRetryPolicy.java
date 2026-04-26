package com.astrsomn.workflow.starter.runtime.policy;

import com.astrsomn.workflow.core.runtime.spi.policy.AstFlowRetryPolicy;

public class NoopAstFlowRetryPolicy implements AstFlowRetryPolicy {

    @Override
    public boolean shouldRetry(String nodeType, int attemptNo, Throwable throwable) {
        return false;
    }
}
