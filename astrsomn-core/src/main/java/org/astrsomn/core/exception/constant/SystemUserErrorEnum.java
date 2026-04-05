package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * System User Error Enum
 */
@Getter
@AllArgsConstructor
public enum SystemUserErrorEnum implements IError {
    /**
     * User not found
     */
    USER_NOT_FOUND(200001, "User not found"),
    
    /**
     * User creation failed
     */
    USER_CREATE_FAILED(200002, "User creation failed"),
    
    /**
     * User update failed
     */
    USER_UPDATE_FAILED(200003, "User update failed"),
    
    /**
     * User deletion failed
     */
    USER_DELETE_FAILED(200004, "User deletion failed"),
    
    /**
     * User parameter error
     */
    USER_PARAM_ERROR(200005, "User parameter error"),
    
    /**
     * User permission denied
     */
    USER_PERMISSION_DENIED(200006, "User permission denied");
    
    private final int code;
    private final String message;
}