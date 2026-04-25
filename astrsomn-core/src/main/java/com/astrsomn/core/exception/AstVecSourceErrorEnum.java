package com.astrsomn.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.commn.base.IError;

/**
 * Vector Source Error Enum
 */
@Getter
@AllArgsConstructor
public enum AstVecSourceErrorEnum implements IError {
    /**
     * Source not found
     */
    SOURCE_NOT_FOUND(150001, "Source not found"),
    
    /**
     * Source creation failed
     */
    SOURCE_CREATE_FAILED(150002, "Source creation failed"),
    
    /**
     * Source update failed
     */
    SOURCE_UPDATE_FAILED(150003, "Source update failed"),
    
    /**
     * Source deletion failed
     */
    SOURCE_DELETE_FAILED(150004, "Source deletion failed"),
    
    /**
     * Source parameter error
     */
    SOURCE_PARAM_ERROR(150005, "Source parameter error"),
    
    /**
     * Source permission denied
     */
    SOURCE_PERMISSION_DENIED(150006, "Source permission denied"),

    /**
     * No VecDriver registered for provider
     */
    VEC_DRIVER_NOT_FOUND(150007, "Vector driver not found for provider");
    
    private final int code;
    private final String message;
}