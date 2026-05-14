package com.astrsomn.api.runtime.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AstroChatErrorEnum implements IError {

    CHAT_NOT_FOUND(20001, "Chat not found"),


    CHAT_CREATE_FAILED(20002, "Chat creation failed"),


    CHAT_UPDATE_FAILED(20003, "Chat update failed"),


    CHAT_DELETE_FAILED(20004, "Chat deletion failed"),


    CHAT_PARAM_ERROR(20005, "Chat parameter error"),


    CHAT_PERMISSION_DENIED(20006, "Chat permission denied");

    private final int code;
    private final String message;
}