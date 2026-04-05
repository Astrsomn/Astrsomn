package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * System Environment Error Enum
 */
@Getter
@AllArgsConstructor
public enum SystemEnvErrorEnum implements IError {
    /**
     * Environment not found
     */
    ENV_NOT_FOUND(180001, "Environment not found"),
    
    /**
     * Environment creation failed
     */
    ENV_CREATE_FAILED(180002, "Environment creation failed"),
    
    /**
     * Environment update failed
     */
    ENV_UPDATE_FAILED(180003, "Environment update failed"),
    
    /**
     * Environment deletion failed
     */
    ENV_DELETE_FAILED(180004, "Environment deletion failed"),
    
    /**
     * Environment parameter error
     */
    ENV_PARAM_ERROR(180005, "Environment parameter error"),
    
    /**
     * Environment permission denied
     */
    ENV_PERMISSION_DENIED(180006, "Environment permission denied");
    
    private final int code;
    private final String message;
}