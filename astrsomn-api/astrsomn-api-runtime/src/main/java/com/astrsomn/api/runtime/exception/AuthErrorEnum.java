package com.astrsomn.api.runtime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.IError;


@Getter
@AllArgsConstructor
public enum AuthErrorEnum implements IError {
    
    AUTH_FAILED(30001, "Authentication failed"),
    
    
    INVALID_TOKEN(30002, "Invalid token"),
    
    
    TOKEN_EXPIRED(30003, "Token expired"),
    
    
    NO_PERMISSION(30004, "No permission"),
    
    
    USER_NOT_FOUND(30005, "User not found"),
    
    
    PASSWORD_ERROR(30006, "Password error");
    
    private final int code;
    private final String message;
}