package org.astrsomn.workflow.core.context;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowExecutionResult {

    @Builder.Default
    private WorkflowStatus status = WorkflowStatus.RUNNING;
    private WorkflowContext context;
    private String lastNodeId;
    private String message;
    private Throwable error;

    public static WorkflowExecutionResult completed(WorkflowContext ctx, String lastNodeId) {
        return WorkflowExecutionResult.builder()
                .status(WorkflowStatus.COMPLETED)
                .context(ctx)
                .lastNodeId(lastNodeId)
                .build();
    }

    public static WorkflowExecutionResult terminated(WorkflowContext ctx, String lastNodeId, String message) {
        return WorkflowExecutionResult.builder()
                .status(WorkflowStatus.TERMINATED)
                .context(ctx)
                .lastNodeId(lastNodeId)
                .message(message)
                .build();
    }

    public static WorkflowExecutionResult failed(WorkflowContext ctx, String lastNodeId, Throwable error) {
        return WorkflowExecutionResult.builder()
                .status(WorkflowStatus.FAILED)
                .context(ctx)
                .lastNodeId(lastNodeId)
                .error(error)
                .message(error != null ? error.getMessage() : null)
                .build();
    }
}
