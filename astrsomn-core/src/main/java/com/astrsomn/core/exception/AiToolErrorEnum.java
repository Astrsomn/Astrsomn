package com.astrsomn.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.commn.base.IError;

/**
 * AI Tool Error Enum
 */
@Getter
@AllArgsConstructor
public enum AiToolErrorEnum implements IError {
    /**
     * Tool not found
     */
    TOOL_NOT_FOUND(100001, "Tool not found"),
    
    /**
     * Tool creation failed
     */
    TOOL_CREATE_FAILED(100002, "Tool creation failed"),
    
    /**
     * Tool update failed
     */
    TOOL_UPDATE_FAILED(100003, "Tool update failed"),
    
    /**
     * Tool deletion failed
     */
    TOOL_DELETE_FAILED(100004, "Tool deletion failed"),
    
    /**
     * Tool parameter error
     */
    TOOL_PARAM_ERROR(100005, "Tool parameter error"),
    
    /**
     * Tool permission denied
     */
    TOOL_PERMISSION_DENIED(100006, "Tool permission denied");
    
    private final int code;
    private final String message;
}