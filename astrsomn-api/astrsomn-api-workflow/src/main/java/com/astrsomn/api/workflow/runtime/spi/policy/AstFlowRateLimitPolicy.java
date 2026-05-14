package com.astrsomn.api.workflow.runtime.spi.policy;

import com.astrsomn.api.workflow.runtime.context.AstFlowExecutionContext;

public interface AstFlowRateLimitPolicy {

    boolean allow(AstFlowExecutionContext context);
}