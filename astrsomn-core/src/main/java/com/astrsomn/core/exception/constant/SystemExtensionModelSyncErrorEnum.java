package com.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.core.exception.base.IError;

/**
 * System Extension Model Sync Error Enum
 */
@Getter
@AllArgsConstructor
public enum SystemExtensionModelSyncErrorEnum implements IError {
    /**
     * Sync failed
     */
    SYNC_FAILED(40001, "Sync failed"),
    
    /**
     * Sync parameter error
     */
    SYNC_PARAM_ERROR(40002, "Sync parameter error"),
    
    /**
     * Sync timeout
     */
    SYNC_TIMEOUT(40003, "Sync timeout"),
    
    /**
     * Sync permission denied
     */
    SYNC_PERMISSION_DENIED(40004, "Sync permission denied"),
    
    /**
     * Model not found
     */
    MODEL_NOT_FOUND(40005, "Model not found"),
    
    /**
     * Extension not found
     */
    EXTENSION_NOT_FOUND(40006, "Extension not found");
    
    private final int code;
    private final String message;
}