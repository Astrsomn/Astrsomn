package com.astrsomn.api.runtime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.IError;


@Getter
@AllArgsConstructor
public enum AiAccountErrorEnum implements IError {
    
    ACCOUNT_NOT_FOUND(10001, "Account not found"),
    
    
    ACCOUNT_CREATE_FAILED(10002, "Account creation failed"),
    
    
    ACCOUNT_UPDATE_FAILED(10003, "Account update failed"),
    
    
    ACCOUNT_DELETE_FAILED(10004, "Account deletion failed"),
    
    
    ACCOUNT_PARAM_ERROR(10005, "Account parameter error"),
    
    
    ACCOUNT_PERMISSION_DENIED(10006, "Account permission denied");
    
    private final int code;
    private final String message;
}