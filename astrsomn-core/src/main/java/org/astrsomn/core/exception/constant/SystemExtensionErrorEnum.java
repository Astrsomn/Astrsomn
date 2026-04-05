package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * System Extension Error Enum
 */
@Getter
@AllArgsConstructor
public enum SystemExtensionErrorEnum implements IError {
    /**
     * Extension not found
     */
    EXTENSION_NOT_FOUND(190001, "Extension not found"),
    
    /**
     * Extension creation failed
     */
    EXTENSION_CREATE_FAILED(190002, "Extension creation failed"),
    
    /**
     * Extension update failed
     */
    EXTENSION_UPDATE_FAILED(190003, "Extension update failed"),
    
    /**
     * Extension deletion failed
     */
    EXTENSION_DELETE_FAILED(190004, "Extension deletion failed"),
    
    /**
     * Extension parameter error
     */
    EXTENSION_PARAM_ERROR(190005, "Extension parameter error"),
    
    /**
     * Extension permission denied
     */
    EXTENSION_PERMISSION_DENIED(190006, "Extension permission denied");
    
    private final int code;
    private final String message;
}