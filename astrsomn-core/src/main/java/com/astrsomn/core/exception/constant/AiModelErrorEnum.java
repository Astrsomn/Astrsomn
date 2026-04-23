package com.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.core.exception.base.IError;

/**
 * AI Model Error Enum
 */
@Getter
@AllArgsConstructor
public enum AiModelErrorEnum implements IError {
    /**
     * Model not found
     */
    MODEL_NOT_FOUND(60001, "Model not found"),
    
    /**
     * Model creation failed
     */
    MODEL_CREATE_FAILED(60002, "Model creation failed"),
    
    /**
     * Model update failed
     */
    MODEL_UPDATE_FAILED(60003, "Model update failed"),
    
    /**
     * Model deletion failed
     */
    MODEL_DELETE_FAILED(60004, "Model deletion failed"),
    
    /**
     * Model parameter error
     */
    MODEL_PARAM_ERROR(60005, "Model parameter error"),
    
    /**
     * Model permission denied
     */
    MODEL_PERMISSION_DENIED(60006, "Model permission denied");
    
    private final int code;
    private final String message;
}