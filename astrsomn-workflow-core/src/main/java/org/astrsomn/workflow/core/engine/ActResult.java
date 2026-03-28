package org.astrsomn.workflow.core.engine;

import org.astrsomn.workflow.core.context.WorkflowExecutionResult;

/**
 * Result of applying one node transition.
 */
final class ActResult {

    final String nextNodeId;
    final String lastEdgeId;
    final WorkflowExecutionResult terminal;
    final boolean stopBeforeEnteringNext;

    private ActResult(String nextNodeId, String lastEdgeId, WorkflowExecutionResult terminal,
                      boolean stopBeforeEnteringNext) {
        this.nextNodeId = nextNodeId;
        this.lastEdgeId = lastEdgeId;
        this.terminal = terminal;
        this.stopBeforeEnteringNext = stopBeforeEnteringNext;
    }

    static ActResult go(String nextNodeId, String lastEdgeId) {
        return new ActResult(nextNodeId, lastEdgeId, null, false);
    }

    static ActResult stopBefore(String nextNodeId, String lastEdgeId) {
        return new ActResult(nextNodeId, lastEdgeId, null, true);
    }

    static ActResult done(WorkflowExecutionResult result) {
        return new ActResult(null, null, result, false);
    }
}
