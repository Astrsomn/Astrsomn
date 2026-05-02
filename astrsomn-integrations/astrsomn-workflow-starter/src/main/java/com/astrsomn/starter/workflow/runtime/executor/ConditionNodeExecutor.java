package com.astrsomn.starter.workflow.runtime.executor;

import com.astrsomn.api.workflow.domain.constant.AstWorkflowEnum;
import com.astrsomn.api.workflow.runtime.constant.AstFlowNodeStateEnum;
import com.astrsomn.api.workflow.runtime.context.AstFlowNodeExecutionContext;
import com.astrsomn.api.workflow.runtime.model.AstFlowNodeExecuteResult;
import com.astrsomn.api.workflow.runtime.spi.AstFlowNodeExecutor;

import java.util.Map;

public class ConditionNodeExecutor implements AstFlowNodeExecutor {

    @Override
    public String type() {
        return AstWorkflowEnum.NodeTypeEnum.CONDITION.getCode();
    }

    @Override
    public AstFlowNodeExecuteResult execute(AstFlowNodeExecutionContext context) {
        Map<String, Object> config = context.getNode().getConfig();
        String trueNextNodeId = config == null ? null : (String) config.get("trueNextNodeId");
        String falseNextNodeId = config == null ? null : (String) config.get("falseNextNodeId");
        Object conditionValue = context.getExecutionContext().getVariables().getOrDefault("condition", Boolean.TRUE);

        String nextNodeId = Boolean.TRUE.equals(conditionValue) ? trueNextNodeId : falseNextNodeId;
        if (nextNodeId == null) {
            nextNodeId = context.getNode().getNextNodeId();
        }

        return AstFlowNodeExecuteResult.builder()
                .nodeState(AstFlowNodeStateEnum.SUCCEEDED)
                .nextNodeId(nextNodeId)
                .message("condition node routed")
                .build();
    }
}
