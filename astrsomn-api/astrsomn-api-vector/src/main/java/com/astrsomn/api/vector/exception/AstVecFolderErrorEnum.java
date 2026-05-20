package com.astrsomn.api.vector.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AstVecFolderErrorEnum implements IError {

    FOLDER_NOT_FOUND(170001, "Folder not found"),


    FOLDER_CREATE_FAILED(170002, "Folder creation failed"),


    FOLDER_UPDATE_FAILED(170003, "Folder update failed"),


    FOLDER_DELETE_FAILED(170004, "Folder deletion failed"),


    FOLDER_PARAM_ERROR(170005, "Folder parameter error");

    private final int code;
    private final String message;
}
