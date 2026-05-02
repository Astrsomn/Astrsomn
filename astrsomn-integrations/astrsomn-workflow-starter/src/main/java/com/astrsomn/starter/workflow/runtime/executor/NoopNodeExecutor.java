package com.astrsomn.starter.workflow.runtime.executor;

import com.astrsomn.api.workflow.runtime.constant.AstFlowNodeStateEnum;
import com.astrsomn.api.workflow.runtime.context.AstFlowNodeExecutionContext;
import com.astrsomn.api.workflow.runtime.model.AstFlowNodeExecuteResult;
import com.astrsomn.api.workflow.runtime.spi.AstFlowNodeExecutor;

public class NoopNodeExecutor implements AstFlowNodeExecutor {

    @Override
    public String type() {
        return "NOOP";
    }

    @Override
    public AstFlowNodeExecuteResult execute(AstFlowNodeExecutionContext context) {
        return AstFlowNodeExecuteResult.builder()
                .nodeState(AstFlowNodeStateEnum.SUCCEEDED)
                .nextNodeId(context.getNode().getNextNodeId())
                .message("fallback noop executor")
                .build();
    }
}
