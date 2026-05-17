package com.astrsomn.api.runtime.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AiChatErrorEnum implements IError {

    CHAT_NOT_FOUND(30001, "Chat record not found"),
    CHAT_CREATE_FAILED(30002, "Chat record creation failed"),
    CHAT_UPDATE_FAILED(30003, "Chat record update failed"),
    CHAT_DELETE_FAILED(30004, "Chat record deletion failed"),
    CHAT_PARAM_ERROR(30005, "Chat parameter error"),
    CHAT_PERMISSION_DENIED(30006, "Chat permission denied");

    private final int code;
    private final String message;
}