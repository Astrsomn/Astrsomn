package com.astrsomn.starter.workflow.runtime.policy;

import com.astrsomn.api.workflow.runtime.context.AstFlowExecutionContext;
import com.astrsomn.api.workflow.runtime.spi.policy.AstFlowRateLimitPolicy;

public class NoopAstFlowRateLimitPolicy implements AstFlowRateLimitPolicy {

    @Override
    public boolean allow(AstFlowExecutionContext context) {
        return true;
    }
}
