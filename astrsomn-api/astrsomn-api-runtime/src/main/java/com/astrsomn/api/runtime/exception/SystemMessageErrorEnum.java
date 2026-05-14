package com.astrsomn.api.runtime.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemMessageErrorEnum implements IError {

    SYSTEM_MESSAGE_NOT_FOUND(81001, "System message not found"),

    SYSTEM_MESSAGE_CREATE_FAILED(81002, "System message creation failed"),

    SYSTEM_MESSAGE_UPDATE_FAILED(81003, "System message update failed"),

    SYSTEM_MESSAGE_DELETE_FAILED(81004, "System message deletion failed"),

    SYSTEM_MESSAGE_PARAM_ERROR(81005, "System message parameter error"),

    SYSTEM_MESSAGE_PERMISSION_DENIED(81006, "System message permission denied");

    private final int code;
    private final String message;
}