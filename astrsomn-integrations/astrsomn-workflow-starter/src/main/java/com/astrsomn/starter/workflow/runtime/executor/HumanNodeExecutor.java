package com.astrsomn.starter.workflow.runtime.executor;

import com.astrsomn.api.workflow.domain.constant.AstWorkflowEnum;
import com.astrsomn.api.workflow.runtime.constant.AstFlowNodeStateEnum;
import com.astrsomn.api.workflow.runtime.context.AstFlowNodeExecutionContext;
import com.astrsomn.api.workflow.runtime.model.AstFlowNodeExecuteResult;
import com.astrsomn.api.workflow.runtime.spi.AstFlowNodeExecutor;

public class HumanNodeExecutor implements AstFlowNodeExecutor {

    @Override
    public String type() {
        return AstWorkflowEnum.NodeTypeEnum.HUMAN.getCode();
    }

    @Override
    public AstFlowNodeExecuteResult execute(AstFlowNodeExecutionContext context) {
        return AstFlowNodeExecuteResult.builder()
                .nodeState(AstFlowNodeStateEnum.WAITING_HUMAN)
                .message("human task created, waiting for approve/reject")
                .build();
    }
}
