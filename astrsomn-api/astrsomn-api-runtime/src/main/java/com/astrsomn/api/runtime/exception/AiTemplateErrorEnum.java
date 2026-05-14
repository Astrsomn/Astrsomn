package com.astrsomn.api.runtime.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AiTemplateErrorEnum implements IError {

    TEMPLATE_NOT_FOUND(90001, "Template not found"),


    TEMPLATE_CREATE_FAILED(90002, "Template creation failed"),


    TEMPLATE_UPDATE_FAILED(90003, "Template update failed"),


    TEMPLATE_DELETE_FAILED(90004, "Template deletion failed"),


    TEMPLATE_PARAM_ERROR(90005, "Template parameter error"),


    TEMPLATE_PERMISSION_DENIED(90006, "Template permission denied");

    private final int code;
    private final String message;
}