package com.astrsomn.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.commn.base.IError;

@Getter
@AllArgsConstructor
public enum AiTraceLogErrorEnum implements IError {

    TRACE_LOG_NOT_FOUND(80001, "Trace log not found"),
    
    TRACE_LOG_CREATE_FAILED(80002, "Trace log creation failed"),
    
    TRACE_LOG_UPDATE_FAILED(80003, "Trace log update failed"),
    
    TRACE_LOG_DELETE_FAILED(80004, "Trace log deletion failed"),
    
    TRACE_LOG_PARAM_ERROR(80005, "Trace log parameter error"),
    
    TRACE_LOG_PERMISSION_DENIED(80006, "Trace log permission denied");
    
    private final int code;
    private final String message;
}
