package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI Workflow Run Error Enum
 */
@Getter
@AllArgsConstructor
public enum AiWorkflowRunErrorEnum implements IError {
    /**
     * Workflow run record not found
     */
    WORKFLOW_RUN_NOT_FOUND(120001, "Workflow run record not found"),
    
    /**
     * Workflow run failed
     */
    WORKFLOW_RUN_FAILED(120002, "Workflow run failed"),
    
    /**
     * Workflow run parameter error
     */
    WORKFLOW_RUN_PARAM_ERROR(120003, "Workflow run parameter error"),
    
    /**
     * Workflow run permission denied
     */
    WORKFLOW_RUN_PERMISSION_DENIED(120004, "Workflow run permission denied");
    
    private final int code;
    private final String message;
}