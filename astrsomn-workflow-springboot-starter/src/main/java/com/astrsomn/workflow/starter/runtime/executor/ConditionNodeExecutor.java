package com.astrsomn.workflow.starter.runtime.executor;

import com.astrsomn.workflow.core.domain.constant.AstWorkflowEnum;
import com.astrsomn.workflow.core.runtime.constant.AstFlowNodeStateEnum;
import com.astrsomn.workflow.core.runtime.context.AstFlowNodeExecutionContext;
import com.astrsomn.workflow.core.runtime.model.AstFlowNodeExecuteResult;
import com.astrsomn.workflow.core.runtime.spi.AstFlowNodeExecutor;

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
