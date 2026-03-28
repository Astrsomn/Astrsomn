package org.astrsomn.workflow.core.spi;

import org.astrsomn.workflow.core.context.WorkflowContext;
import org.astrsomn.workflow.core.model.FlowEdge;
import org.astrsomn.workflow.core.model.FlowNode;

import java.util.List;

/**
 * Chooses {@link FlowEdge#getSourceHandle()} for outgoing edges from a {@link org.astrsomn.workflow.core.model.FlowNodeType#CONDITION} node.
 */
@FunctionalInterface
public interface ConditionEvaluator {

    String resolveSourceHandle(WorkflowContext context, FlowNode node, List<FlowEdge> outgoingEdges) throws Exception;
}
