package org.astrsomn.workflow.core.spi;

import org.astrsomn.workflow.core.context.WorkflowContext;
import org.astrsomn.workflow.core.model.FlowNode;
import org.astrsomn.workflow.core.model.FlowNodeType;

/**
 * Pluggable behaviour for {@link FlowNodeType#TASK} (and custom semantic types if registered).
 */
@FunctionalInterface
public interface NodeHandler {

    /**
     * @param nodeType resolved semantic type for this invocation
     */
    void handle(FlowNodeType nodeType, FlowNode node, WorkflowContext context) throws Exception;
}
