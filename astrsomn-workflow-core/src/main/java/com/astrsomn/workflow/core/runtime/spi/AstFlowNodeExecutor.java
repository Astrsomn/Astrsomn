package com.astrsomn.workflow.core.runtime.spi;

import com.astrsomn.workflow.core.runtime.context.AstFlowNodeExecutionContext;
import com.astrsomn.workflow.core.runtime.model.AstFlowNodeExecuteResult;

public interface AstFlowNodeExecutor {

    String type();

    AstFlowNodeExecuteResult execute(AstFlowNodeExecutionContext context);
}
