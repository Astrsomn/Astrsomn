package com.astrsomn.api.runtime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.IError;


@Getter
@AllArgsConstructor
public enum AiMcpErrorEnum implements IError {
    
    MCP_NOT_FOUND(50001, "MCP not found"),
    
    
    MCP_CREATE_FAILED(50002, "MCP creation failed"),
    
    
    MCP_UPDATE_FAILED(50003, "MCP update failed"),
    
    
    MCP_DELETE_FAILED(50004, "MCP deletion failed"),
    
    
    MCP_PARAM_ERROR(50005, "MCP parameter error"),
    
    
    MCP_PERMISSION_DENIED(50006, "MCP permission denied");
    
    private final int code;
    private final String message;
}