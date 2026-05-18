package com.astrsomn.starter.workflow.runtime.executor;

import com.astrsomn.api.workflow.domain.constant.AstWorkflowEnum;
import com.astrsomn.api.workflow.runtime.constant.AstFlowNodeStateEnum;
import com.astrsomn.api.workflow.runtime.context.AstFlowNodeExecutionContext;
import com.astrsomn.api.workflow.runtime.model.AstFlowNodeExecuteResult;
import com.astrsomn.api.workflow.runtime.spi.AstFlowNodeExecutor;

import java.util.HashMap;
import java.util.Map;

public class ToolNodeExecutor implements AstFlowNodeExecutor {

    @Override
    public String type() {
        return AstWorkflowEnum.NodeTypeEnum.TOOL.getCode();
    }

    @Override
    public AstFlowNodeExecuteResult execute(AstFlowNodeExecutionContext context) {



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
