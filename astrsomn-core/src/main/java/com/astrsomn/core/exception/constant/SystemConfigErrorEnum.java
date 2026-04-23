package com.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.core.exception.base.IError;

@Getter
@AllArgsConstructor
public enum SystemConfigErrorEnum implements IError {
    CONFIG_NOT_FOUND(50001, "Config not found"),
    CONFIG_CREATE_FAILED(50002, "Config creation failed"),
    CONFIG_UPDATE_FAILED(50003, "Config update failed"),
    CONFIG_DELETE_FAILED(50004, "Config deletion failed"),
    CONFIG_PARAM_ERROR(50005, "Config parameter error"),
    CONFIG_PERMISSION_DENIED(50006, "Config permission denied");

    private final int code;
    private final String message;
}
