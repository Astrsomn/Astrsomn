package com.astrsomn.workflow.starter.runtime.plan;

import com.astrsomn.workflow.core.domain.constant.AstWorkflowEnum;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.workflow.core.runtime.model.AstFlowExecutableNode;
import com.astrsomn.workflow.core.runtime.model.AstFlowExecutablePlan;
import com.astrsomn.workflow.core.runtime.spi.AstFlowPlanResolver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DefaultAstFlowPlanResolver implements AstFlowPlanResolver {

    @Override
    public AstFlowExecutablePlan resolve(AstFlowTestRunRequestDTO request) {
        // Pseudo-code for future deployment resolution:
        // 1. if request.id != null -> load draft/deployment by id
        // 2. if request.workflowKey != null -> load latest deployment by key
        // 3. parse compiled plan json -> build executable graph
        String planId = request.getWorkflowKey() == null ? UUID.randomUUID().toString() : request.getWorkflowKey();

        AstFlowExecutableNode startNode = AstFlowExecutableNode.builder()
                .nodeId("start")
                .nodeType(AstWorkflowEnum.NodeTypeEnum.START.getCode())
                .nextNodeId("llm")
                .build();
        AstFlowExecutableNode llmNode = AstFlowExecutableNode.builder()
                .nodeId("llm")
                .nodeType(AstWorkflowEnum.NodeTypeEnum.LLM.getCode())
                .nextNodeId("end")
                .build();
        AstFlowExecutableNode endNode = AstFlowExecutableNode.builder()
                .nodeId("end")
                .nodeType(AstWorkflowEnum.NodeTypeEnum.END.getCode())
                .build();

        Map<String, AstFlowExecutableNode> nodes = new LinkedHashMap<>();
        nodes.put(startNode.getNodeId(), startNode);
        nodes.put(llmNode.getNodeId(), llmNode);
        nodes.put(endNode.getNodeId(), endNode);

        return AstFlowExecutablePlan.builder()
                .planId(planId)
                .startNodeId(startNode.getNodeId())
                .nodes(nodes)
                .orderedNodes(List.of(startNode, llmNode, endNode))
                .build();
    }
}
