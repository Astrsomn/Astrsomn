package com.astrsomn.starter.workflow.runtime.policy;

import com.astrsomn.api.workflow.runtime.spi.policy.AstFlowRetryPolicy;

public class NoopAstFlowRetryPolicy implements AstFlowRetryPolicy {

    @Override
    public boolean shouldRetry(String nodeType, int attemptNo, Throwable throwable) {
        return false;
    }
}
