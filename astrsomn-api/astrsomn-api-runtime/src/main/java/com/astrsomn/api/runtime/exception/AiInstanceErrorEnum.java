package com.astrsomn.api.runtime.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AiInstanceErrorEnum implements IError {

    INSTANCE_NOT_FOUND(40001, "Instance not found"),


    INSTANCE_CREATE_FAILED(40002, "Instance creation failed"),


    INSTANCE_UPDATE_FAILED(40003, "Instance update failed"),


    INSTANCE_DELETE_FAILED(40004, "Instance deletion failed"),


    INSTANCE_PARAM_ERROR(40005, "Instance parameter error"),


    INSTANCE_PERMISSION_DENIED(40006, "Instance permission denied");

    private final int code;
    private final String message;
}