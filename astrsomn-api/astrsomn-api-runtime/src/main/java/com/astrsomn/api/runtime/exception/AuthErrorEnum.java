package com.astrsomn.api.runtime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.IError;

/**
 * Auth Error Enum
 */
@Getter
@AllArgsConstructor
public enum AuthErrorEnum implements IError {
    /**
     * Authentication failed
     */
    AUTH_FAILED(30001, "Authentication failed"),
    
    /**
     * Invalid token
     */
    INVALID_TOKEN(30002, "Invalid token"),
    
    /**
     * Token expired
     */
    TOKEN_EXPIRED(30003, "Token expired"),
    
    /**
     * No permission
     */
    NO_PERMISSION(30004, "No permission"),
    
    /**
     * User not found
     */
    USER_NOT_FOUND(30005, "User not found"),
    
    /**
     * Password error
     */
    PASSWORD_ERROR(30006, "Password error");
    
    private final int code;
    private final String message;
}