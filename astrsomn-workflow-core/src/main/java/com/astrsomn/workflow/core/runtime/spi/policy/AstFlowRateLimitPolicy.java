package com.astrsomn.workflow.core.runtime.spi.policy;

import com.astrsomn.workflow.core.runtime.context.AstFlowExecutionContext;

public interface AstFlowRateLimitPolicy {

    boolean allow(AstFlowExecutionContext context);
}
