package com.astrsomn.starter.workflow.runtime.policy;

import com.astrsomn.api.workflow.runtime.spi.policy.AstFlowTimeoutPolicy;

public class NoopAstFlowTimeoutPolicy implements AstFlowTimeoutPolicy {

    @Override
    public long timeoutMs(String nodeType) {
        return 0L;
    }
}
