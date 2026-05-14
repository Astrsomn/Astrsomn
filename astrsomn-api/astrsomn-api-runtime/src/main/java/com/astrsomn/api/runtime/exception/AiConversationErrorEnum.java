package com.astrsomn.api.runtime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.IError;


@Getter
@AllArgsConstructor
public enum AiConversationErrorEnum implements IError {
    
    CONVERSATION_NOT_FOUND(30001, "Conversation not found"),
    
    
    CONVERSATION_CREATE_FAILED(30002, "Conversation creation failed"),
    
    
    CONVERSATION_UPDATE_FAILED(30003, "Conversation update failed"),
    
    
    CONVERSATION_DELETE_FAILED(30004, "Conversation deletion failed"),
    
    
    CONVERSATION_PARAM_ERROR(30005, "Conversation parameter error"),
    
    
    CONVERSATION_PERMISSION_DENIED(30006, "Conversation permission denied");
    
    private final int code;
    private final String message;
}