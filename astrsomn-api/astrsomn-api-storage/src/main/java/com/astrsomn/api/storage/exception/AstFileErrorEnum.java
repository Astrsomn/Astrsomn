package com.astrsomn.api.storage.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AstFileErrorEnum implements IError {

    FILE_NOT_FOUND(140001, "File not found"),
    FILE_UPLOAD_FAILED(140002, "File upload failed"),
    FILE_DELETE_FAILED(140003, "File delete failed"),
    FILE_RECORD_CREATE_FAILED(140004, "File record create failed"),
    FILE_PARAM_ERROR(140005, "File parameter error");

    private final int code;
    private final String message;
}
