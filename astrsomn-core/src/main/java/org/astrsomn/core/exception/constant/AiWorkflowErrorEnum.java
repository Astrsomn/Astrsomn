package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI Workflow Error Enum
 */
@Getter
@AllArgsConstructor
public enum AiWorkflowErrorEnum implements IError {
    /**
     * Workflow not found
     */
    WORKFLOW_NOT_FOUND(110001, "Workflow not found"),
    
    /**
     * Workflow creation failed
     */
    WORKFLOW_CREATE_FAILED(110002, "Workflow creation failed"),
    
    /**
     * Workflow update failed
     */
    WORKFLOW_UPDATE_FAILED(110003, "Workflow update failed"),
    
    /**
     * Workflow deletion failed
     */
    WORKFLOW_DELETE_FAILED(110004, "Workflow deletion failed"),
    
    /**
     * Workflow parameter error
     */
    WORKFLOW_PARAM_ERROR(110005, "Workflow parameter error"),
    
    /**
     * Workflow permission denied
     */
    WORKFLOW_PERMISSION_DENIED(110006, "Workflow permission denied"),

    /**
     *
     */
    WORKFLOW_EXECUTION_FAILED(110007, "WorkFlow execution failed" );
    
    private final int code;
    private final String message;
}