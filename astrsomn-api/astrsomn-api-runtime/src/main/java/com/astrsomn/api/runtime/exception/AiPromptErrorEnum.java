package com.astrsomn.api.runtime.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AiPromptErrorEnum implements IError {

    PROMPT_NOT_FOUND(70001, "Prompt not found"),


    PROMPT_CREATE_FAILED(70002, "Prompt creation failed"),


    PROMPT_UPDATE_FAILED(70003, "Prompt update failed"),


    PROMPT_DELETE_FAILED(70004, "Prompt deletion failed"),


    PROMPT_PARAM_ERROR(70005, "Prompt parameter error"),


    PROMPT_PERMISSION_DENIED(70006, "Prompt permission denied");

    private final int code;
    private final String message;
}