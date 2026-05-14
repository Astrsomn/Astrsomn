package com.astrsomn.api.runtime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.IError;


@Getter
@AllArgsConstructor
public enum AiModelErrorEnum implements IError {
    
    MODEL_NOT_FOUND(60001, "Model not found"),
    
    
    MODEL_CREATE_FAILED(60002, "Model creation failed"),
    
    
    MODEL_UPDATE_FAILED(60003, "Model update failed"),
    
    
    MODEL_DELETE_FAILED(60004, "Model deletion failed"),
    
    
    MODEL_PARAM_ERROR(60005, "Model parameter error"),
    
    
    MODEL_PERMISSION_DENIED(60006, "Model permission denied");
    
    private final int code;
    private final String message;
}