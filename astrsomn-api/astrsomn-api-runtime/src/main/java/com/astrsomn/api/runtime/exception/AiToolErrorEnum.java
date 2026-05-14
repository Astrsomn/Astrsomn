package com.astrsomn.api.runtime.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AiToolErrorEnum implements IError {

    TOOL_NOT_FOUND(100001, "Tool not found"),


    TOOL_CREATE_FAILED(100002, "Tool creation failed"),


    TOOL_UPDATE_FAILED(100003, "Tool update failed"),


    TOOL_DELETE_FAILED(100004, "Tool deletion failed"),


    TOOL_PARAM_ERROR(100005, "Tool parameter error"),


    TOOL_PERMISSION_DENIED(100006, "Tool permission denied");

    private final int code;
    private final String message;
}