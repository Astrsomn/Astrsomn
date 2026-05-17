package com.astrsomn.api.workflow.runtime.spi;

import com.astrsomn.api.workflow.runtime.context.AstFlowNodeExecutionContext;
import com.astrsomn.api.workflow.runtime.model.AstFlowNodeExecuteResult;

public interface AstFlowNodeExecutor {

    String type();

    AstFlowNodeExecuteResult execute(AstFlowNodeExecutionContext context);
}