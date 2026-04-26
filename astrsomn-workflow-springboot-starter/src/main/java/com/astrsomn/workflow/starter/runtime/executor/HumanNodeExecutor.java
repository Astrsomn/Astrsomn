package com.astrsomn.workflow.starter.runtime.executor;

import com.astrsomn.workflow.core.domain.constant.AstWorkflowEnum;
import com.astrsomn.workflow.core.runtime.constant.AstFlowNodeStateEnum;
import com.astrsomn.workflow.core.runtime.context.AstFlowNodeExecutionContext;
import com.astrsomn.workflow.core.runtime.model.AstFlowNodeExecuteResult;
import com.astrsomn.workflow.core.runtime.spi.AstFlowNodeExecutor;

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
