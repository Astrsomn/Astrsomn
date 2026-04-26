package com.astrsomn.workflow.starter.runtime.executor;

import com.astrsomn.workflow.core.runtime.constant.AstFlowNodeStateEnum;
import com.astrsomn.workflow.core.runtime.context.AstFlowNodeExecutionContext;
import com.astrsomn.workflow.core.runtime.model.AstFlowNodeExecuteResult;
import com.astrsomn.workflow.core.runtime.spi.AstFlowNodeExecutor;

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
