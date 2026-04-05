package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * System Config Error Enum
 */
@Getter
@AllArgsConstructor
public enum SystemConfigErrorEnum implements IError {
    /**
     * Config not found
     */
    CONFIG_NOT_FOUND(170001, "Config not found"),
    
    /**
     * Config creation failed
     */
    CONFIG_CREATE_FAILED(170002, "Config creation failed"),
    
    /**
     * Config update failed
     */
    CONFIG_UPDATE_FAILED(170003, "Config update failed"),
    
    /**
     * Config deletion failed
     */
    CONFIG_DELETE_FAILED(170004, "Config deletion failed"),
    
    /**
     * Config parameter error
     */
    CONFIG_PARAM_ERROR(170005, "Config parameter error"),
    
    /**
     * Config permission denied
     */
    CONFIG_PERMISSION_DENIED(170006, "Config permission denied");
    
    private final int code;
    private final String message;
}