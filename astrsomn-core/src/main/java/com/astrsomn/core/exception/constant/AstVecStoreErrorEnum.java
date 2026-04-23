package com.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.core.exception.base.IError;

/**
 * Vector Store Error Enum
 */
@Getter
@AllArgsConstructor
public enum AstVecStoreErrorEnum implements IError {
    /**
     * Store not found
     */
    STORE_NOT_FOUND(160001, "Store not found"),
    
    /**
     * Store creation failed
     */
    STORE_CREATE_FAILED(160002, "Store creation failed"),
    
    /**
     * Store update failed
     */
    STORE_UPDATE_FAILED(160003, "Store update failed"),
    
    /**
     * Store deletion failed
     */
    STORE_DELETE_FAILED(160004, "Store deletion failed"),
    
    /**
     * Store parameter error
     */
    STORE_PARAM_ERROR(160005, "Store parameter error"),
    
    /**
     * Store permission denied
     */
    STORE_PERMISSION_DENIED(160006, "Store permission denied");
    
    private final int code;
    private final String message;
}