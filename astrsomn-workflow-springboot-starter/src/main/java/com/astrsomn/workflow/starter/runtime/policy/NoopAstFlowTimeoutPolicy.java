package com.astrsomn.workflow.starter.runtime.policy;

import com.astrsomn.workflow.core.runtime.spi.policy.AstFlowTimeoutPolicy;

public class NoopAstFlowTimeoutPolicy implements AstFlowTimeoutPolicy {

    @Override
    public long timeoutMs(String nodeType) {
        return 0L;
    }
}
