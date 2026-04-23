package com.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.core.exception.base.IError;

/**
 * AI Account Error Enum
 */
@Getter
@AllArgsConstructor
public enum AiAccountErrorEnum implements IError {
    /**
     * Account not found
     */
    ACCOUNT_NOT_FOUND(10001, "Account not found"),
    
    /**
     * Account creation failed
     */
    ACCOUNT_CREATE_FAILED(10002, "Account creation failed"),
    
    /**
     * Account update failed
     */
    ACCOUNT_UPDATE_FAILED(10003, "Account update failed"),
    
    /**
     * Account deletion failed
     */
    ACCOUNT_DELETE_FAILED(10004, "Account deletion failed"),
    
    /**
     * Account parameter error
     */
    ACCOUNT_PARAM_ERROR(10005, "Account parameter error"),
    
    /**
     * Account permission denied
     */
    ACCOUNT_PERMISSION_DENIED(10006, "Account permission denied");
    
    private final int code;
    private final String message;
}