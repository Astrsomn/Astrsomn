package com.astrsomn.api.vector.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AstVecSourceErrorEnum implements IError {

    SOURCE_NOT_FOUND(150001, "Source not found"),


    SOURCE_CREATE_FAILED(150002, "Source creation failed"),


    SOURCE_UPDATE_FAILED(150003, "Source update failed"),


    SOURCE_DELETE_FAILED(150004, "Source deletion failed"),


    SOURCE_PARAM_ERROR(150005, "Source parameter error"),


    SOURCE_PERMISSION_DENIED(150006, "Source permission denied"),


    VEC_DRIVER_NOT_FOUND(150007, "Vector driver not found for provider");

    private final int code;
    private final String message;
}