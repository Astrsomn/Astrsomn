package com.astrsomn.workflow.starter.runtime.policy;

import com.astrsomn.workflow.core.runtime.context.AstFlowExecutionContext;
import com.astrsomn.workflow.core.runtime.spi.policy.AstFlowRateLimitPolicy;

public class NoopAstFlowRateLimitPolicy implements AstFlowRateLimitPolicy {

    @Override
    public boolean allow(AstFlowExecutionContext context) {
        return true;
    }
}
