package com.astrsomn.workflow.starter.runtime.executor;

import com.astrsomn.workflow.core.domain.constant.AstWorkflowEnum;
import com.astrsomn.workflow.core.runtime.constant.AstFlowNodeStateEnum;
import com.astrsomn.workflow.core.runtime.context.AstFlowNodeExecutionContext;
import com.astrsomn.workflow.core.runtime.model.AstFlowNodeExecuteResult;
import com.astrsomn.workflow.core.runtime.spi.AstFlowNodeExecutor;

import java.util.HashMap;
import java.util.Map;

public class LlmNodeExecutor implements AstFlowNodeExecutor {

    @Override
    public String type() {
        return AstWorkflowEnum.NodeTypeEnum.LLM.getCode();
    }

    @Override
    public AstFlowNodeExecuteResult execute(AstFlowNodeExecutionContext context) {
        // Pseudo-code for future provider invocation:
        // llmResult = llmClient.chat(promptTemplate, context.variables)
        // context.variables.put("llmOutput", llmResult.text())
        Map<String, Object> output = new HashMap<>();
        output.put("llmOutput", "mock-llm-output");

        return AstFlowNodeExecuteResult.builder()
                .nodeState(AstFlowNodeStateEnum.SUCCEEDED)
                .nextNodeId(context.getNode().getNextNodeId())
                .message("llm node executed with mock output")
                .outputVariables(output)
                .build();
    }
}
