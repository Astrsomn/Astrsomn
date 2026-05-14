package com.astrsomn.api.runtime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.IError;


@Getter
@AllArgsConstructor
public enum AstVecStoreErrorEnum implements IError {
    
    STORE_NOT_FOUND(160001, "Store not found"),
    
    
    STORE_CREATE_FAILED(160002, "Store creation failed"),
    
    
    STORE_UPDATE_FAILED(160003, "Store update failed"),
    
    
    STORE_DELETE_FAILED(160004, "Store deletion failed"),
    
    
    STORE_PARAM_ERROR(160005, "Store parameter error"),
    
    
    STORE_PERMISSION_DENIED(160006, "Store permission denied");
    
    private final int code;
    private final String message;
}