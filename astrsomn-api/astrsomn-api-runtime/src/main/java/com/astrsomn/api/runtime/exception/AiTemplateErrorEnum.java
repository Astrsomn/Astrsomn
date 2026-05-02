package com.astrsomn.api.runtime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.IError;

/**
 * AI Template Error Enum
 */
@Getter
@AllArgsConstructor
public enum AiTemplateErrorEnum implements IError {
    /**
     * Template not found
     */
    TEMPLATE_NOT_FOUND(90001, "Template not found"),
    
    /**
     * Template creation failed
     */
    TEMPLATE_CREATE_FAILED(90002, "Template creation failed"),
    
    /**
     * Template update failed
     */
    TEMPLATE_UPDATE_FAILED(90003, "Template update failed"),
    
    /**
     * Template deletion failed
     */
    TEMPLATE_DELETE_FAILED(90004, "Template deletion failed"),
    
    /**
     * Template parameter error
     */
    TEMPLATE_PARAM_ERROR(90005, "Template parameter error"),
    
    /**
     * Template permission denied
     */
    TEMPLATE_PERMISSION_DENIED(90006, "Template permission denied");
    
    private final int code;
    private final String message;
}