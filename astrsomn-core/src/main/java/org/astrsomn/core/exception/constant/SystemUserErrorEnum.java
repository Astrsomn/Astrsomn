package org.astrsomn.core.exception.constant;

import org.astrsomn.core.exception.base.IError;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemUserErrorEnum implements IError {
    USER_NOT_FOUND(80001, "User not found"),
    USER_CREATE_FAILED(80002, "User creation failed"),
    USER_UPDATE_FAILED(80003, "User update failed"),
    USER_DELETE_FAILED(80004, "User deletion failed"),
    USER_PARAM_ERROR(80005, "User parameter error"),
    USER_PERMISSION_DENIED(80006, "User permission denied");

    private final int code;
    private final String message;
}
