package com.astrsomn.api.runtime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.IError;

@Getter
@AllArgsConstructor
public enum SystemExtensionErrorEnum implements IError {
    EXTENSION_NOT_FOUND(70001, "Extension not found"),
    EXTENSION_CREATE_FAILED(70002, "Extension creation failed"),
    EXTENSION_UPDATE_FAILED(70003, "Extension update failed"),
    EXTENSION_DELETE_FAILED(70004, "Extension deletion failed"),
    EXTENSION_PARAM_ERROR(70005, "Extension parameter error"),
    EXTENSION_PERMISSION_DENIED(70006, "Extension permission denied"),
    EXTENSION_APPLY_FAILED(70007, "Extension apply failed"),
    EXTENSION_REVOKE_FAILED(70008, "Extension revoke failed"),
    EXTENSION_UNINSTALL_FAILED(70009, "Extension uninstall failed"),
    EXTENSION_UPLOAD_FAILED(70010, "Extension upload failed");

    private final int code;
    private final String message;
}