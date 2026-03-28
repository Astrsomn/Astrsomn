package org.astrsomn.workflow.core.engine;

import org.astrsomn.workflow.core.context.WorkflowContext;
import org.astrsomn.workflow.core.context.WorkflowExecutionResult;
import org.astrsomn.workflow.core.model.WorkflowDefinition;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Sync and async workflow execution entry points.
 */
public interface WorkflowEngine {

    WorkflowExecutionResult execute(WorkflowDefinition definition, WorkflowContext context);

    default CompletableFuture<WorkflowExecutionResult> executeAsync(WorkflowDefinition definition,
                                                                    WorkflowContext context,
                                                                    Executor executor) {
        Executor ex = executor != null ? executor : Runnable::run;
        return CompletableFuture.supplyAsync(() -> execute(definition, context), ex);
    }
}
