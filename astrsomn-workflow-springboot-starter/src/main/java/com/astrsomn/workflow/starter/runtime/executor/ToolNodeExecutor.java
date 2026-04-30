package com.astrsomn.workflow.starter.runtime.executor;

import com.astrsomn.workflow.core.domain.constant.AstWorkflowEnum;
import com.astrsomn.workflow.core.runtime.constant.AstFlowNodeStateEnum;
import com.astrsomn.workflow.core.runtime.context.AstFlowNodeExecutionContext;
import com.astrsomn.workflow.core.runtime.model.AstFlowNodeExecuteResult;
import com.astrsomn.workflow.core.runtime.spi.AstFlowNodeExecutor;

import java.util.HashMap;
import java.util.Map;

public class ToolNodeExecutor implements AstFlowNodeExecutor {

    @Override
    public String type() {
        return AstWorkflowEnum.NodeTypeEnum.TOOL.getCode();
    }

    @Override
    public AstFlowNodeExecuteResult execute(AstFlowNodeExecutionContext context) {
        // Pseudo-code for future tool invocation:
        // toolResult = toolRouter.invoke(toolName, context.variables)
        // context.variables.put("toolOutput", toolResult)
        Map<String, Object> output = new HashMap<>();
        output.put("toolOutput", "mock-tool-output");

        return AstFlowNodeExecuteResult.builder()
                .nodeState(AstFlowNodeStateEnum.SUCCEEDED)
                .nextNodeId(context.getNode().getNextNodeId())
                .message("tool node executed with mock output")
                .outputVariables(output)
                .build();
    }
}
