package com.astrsomn.api.runtime.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemEnvErrorEnum implements IError {
    ENV_NOT_FOUND(60001, "Environment not found"),
    ENV_CREATE_FAILED(60002, "Environment creation failed"),
    ENV_UPDATE_FAILED(60003, "Environment update failed"),
    ENV_DELETE_FAILED(60004, "Environment deletion failed"),
    ENV_PARAM_ERROR(60005, "Environment parameter error"),
    ENV_PERMISSION_DENIED(60006, "Environment permission denied");

    private final int code;
    private final String message;
}