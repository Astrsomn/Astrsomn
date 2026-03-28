package org.astrsomn.workflow.core.spi;

import org.astrsomn.workflow.core.context.WorkflowContext;
import org.astrsomn.workflow.core.model.FlowNode;

/**
 * Decides loop body vs exit after iteration bookkeeping (optional; engine has safe defaults).
 */
@FunctionalInterface
public interface LoopContinuationPolicy {

    /**
     * @param iteration 1-based iteration count for this loop node
     * @return sourceHandle on outgoing edges: typically {@code body} or {@code exit}
     */
    String resolve(WorkflowContext context, FlowNode loopNode, int iteration, boolean enteredViaLoopBackEdge)
            throws Exception;
}
