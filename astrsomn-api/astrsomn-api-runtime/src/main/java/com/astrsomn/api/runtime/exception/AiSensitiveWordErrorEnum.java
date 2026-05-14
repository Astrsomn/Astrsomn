package com.astrsomn.api.runtime.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AiSensitiveWordErrorEnum implements IError {

    SENSITIVE_WORD_NOT_FOUND(80001, "Sensitive word not found"),


    SENSITIVE_WORD_CREATE_FAILED(80002, "Sensitive word creation failed"),


    SENSITIVE_WORD_UPDATE_FAILED(80003, "Sensitive word update failed"),


    SENSITIVE_WORD_DELETE_FAILED(80004, "Sensitive word deletion failed"),


    SENSITIVE_WORD_PARAM_ERROR(80005, "Sensitive word parameter error"),


    SENSITIVE_WORD_PERMISSION_DENIED(80006, "Sensitive word permission denied");

    private final int code;
    private final String message;
}