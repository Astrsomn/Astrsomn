package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI Instance Error Enum
 */
@Getter
@AllArgsConstructor
public enum AiInstanceErrorEnum implements IError {
    /**
     * Instance not found
     */
    INSTANCE_NOT_FOUND(40001, "Instance not found"),
    
    /**
     * Instance creation failed
     */
    INSTANCE_CREATE_FAILED(40002, "Instance creation failed"),
    
    /**
     * Instance update failed
     */
    INSTANCE_UPDATE_FAILED(40003, "Instance update failed"),
    
    /**
     * Instance deletion failed
     */
    INSTANCE_DELETE_FAILED(40004, "Instance deletion failed"),
    
    /**
     * Instance parameter error
     */
    INSTANCE_PARAM_ERROR(40005, "Instance parameter error"),
    
    /**
     * Instance permission denied
     */
    INSTANCE_PERMISSION_DENIED(40006, "Instance permission denied");
    
    private final int code;
    private final String message;
}