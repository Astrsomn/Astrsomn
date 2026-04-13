package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI Agent Error Enum
 */
@Getter
@AllArgsConstructor
public enum AiAgentErrorEnum implements IError {
    /**
     * Agent not found
     */
    AGENT_NOT_FOUND(20001, "Agent not found"),
    
    /**
     * Agent creation failed
     */
    AGENT_CREATE_FAILED(20002, "Agent creation failed"),
    
    /**
     * Agent update failed
     */
    AGENT_UPDATE_FAILED(20003, "Agent update failed"),
    
    /**
     * Agent deletion failed
     */
    AGENT_DELETE_FAILED(20004, "Agent deletion failed"),
    
    /**
     * Agent parameter error
     */
    AGENT_PARAM_ERROR(20005, "Agent parameter error"),
    
    /**
     * Agent permission denied
     */
    AGENT_PERMISSION_DENIED(20006, "Agent permission denied");
    
    private final int code;
    private final String message;
}