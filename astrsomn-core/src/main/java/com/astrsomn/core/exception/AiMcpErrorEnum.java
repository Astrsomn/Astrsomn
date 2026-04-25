package com.astrsomn.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.commn.base.IError;

/**
 * AI MCP Error Enum
 */
@Getter
@AllArgsConstructor
public enum AiMcpErrorEnum implements IError {
    /**
     * MCP not found
     */
    MCP_NOT_FOUND(50001, "MCP not found"),
    
    /**
     * MCP creation failed
     */
    MCP_CREATE_FAILED(50002, "MCP creation failed"),
    
    /**
     * MCP update failed
     */
    MCP_UPDATE_FAILED(50003, "MCP update failed"),
    
    /**
     * MCP deletion failed
     */
    MCP_DELETE_FAILED(50004, "MCP deletion failed"),
    
    /**
     * MCP parameter error
     */
    MCP_PARAM_ERROR(50005, "MCP parameter error"),
    
    /**
     * MCP permission denied
     */
    MCP_PERMISSION_DENIED(50006, "MCP permission denied");
    
    private final int code;
    private final String message;
}