package com.astrsomn.api.runtime.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AstVecDocErrorEnum implements IError {

    DOC_NOT_FOUND(130001, "Document not found"),


    DOC_CREATE_FAILED(130002, "Document creation failed"),


    DOC_UPDATE_FAILED(130003, "Document update failed"),


    DOC_DELETE_FAILED(130004, "Document deletion failed"),


    DOC_PARAM_ERROR(130005, "Document parameter error"),


    DOC_PERMISSION_DENIED(130006, "Document permission denied"),


    DOC_STORE_NOT_FOUND(130007, "Vector store not found"),


    DOC_SOURCE_NOT_READY(130008, "Vector source not ready"),


    DOC_VECTORIZE_STATUS_INVALID(130009, "Document is not pending vectorization"),


    DOC_FILE_NOT_READABLE(130010, "Document file not readable"),


    DOC_FILE_NOT_TEXT(130011, "Document file is not supported as UTF-8 text"),


    DOC_VECTORIZE_FAILED(130012, "Document vectorization failed");

    private final int code;
    private final String message;
}