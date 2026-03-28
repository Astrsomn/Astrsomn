package org.astrsomn.workflow.starter;

import lombok.RequiredArgsConstructor;
import org.astrsomn.workflow.core.context.WorkflowContext;
import org.astrsomn.workflow.core.context.WorkflowExecutionResult;
import org.astrsomn.workflow.core.engine.DefaultWorkflowEngine;
import org.astrsomn.workflow.core.engine.WorkflowEngine;
import org.astrsomn.workflow.core.engine.WorkflowGraph;
import org.astrsomn.workflow.core.model.WorkflowDefinition;
import org.astrsomn.workflow.core.spi.NodeHandlerRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Symmetric to {@code AstroAssistantFactory}: single entry to obtain a configured {@link WorkflowEngine}
 * and optional handler registry for programmatic node wiring.
 */
@Service
@RequiredArgsConstructor
public class WorkflowRuntimeFactory {

    private final WorkflowEngine workflowEngine;
    private final DefaultWorkflowEngine defaultWorkflowEngine;
    private final NodeHandlerRegistry nodeHandlerRegistry;

    public WorkflowEngine engine() {
        return workflowEngine;
    }

    public NodeHandlerRegistry handlers() {
        return nodeHandlerRegistry;
    }

    public WorkflowExecutionResult run(WorkflowDefinition definition, WorkflowContext context) {
        return workflowEngine.execute(definition, context);
    }

    public CompletableFuture<WorkflowExecutionResult> runAsync(WorkflowDefinition definition,
                                                               WorkflowContext context,
                                                               Executor executor) {
        return workflowEngine.executeAsync(definition, context, executor);
    }

    /**
     * Parallel branches or subgraph execution with an explicit merge boundary.
     */
    public WorkflowExecutionResult runUntil(WorkflowDefinition definition, WorkflowContext context,
                                            String startId, String untilExclusive) {
        return defaultWorkflowEngine.run(new WorkflowGraph(definition), context, startId, untilExclusive);
    }
}
