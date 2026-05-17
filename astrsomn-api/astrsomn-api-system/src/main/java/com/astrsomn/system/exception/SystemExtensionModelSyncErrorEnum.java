package com.astrsomn.system.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum SystemExtensionModelSyncErrorEnum implements IError {

    SYNC_FAILED(40001, "Sync failed"),


    SYNC_PARAM_ERROR(40002, "Sync parameter error"),


    SYNC_TIMEOUT(40003, "Sync timeout"),


    SYNC_PERMISSION_DENIED(40004, "Sync permission denied"),


    MODEL_NOT_FOUND(40005, "Model not found"),


    EXTENSION_NOT_FOUND(40006, "Extension not found");

    private final int code;
    private final String message;
}