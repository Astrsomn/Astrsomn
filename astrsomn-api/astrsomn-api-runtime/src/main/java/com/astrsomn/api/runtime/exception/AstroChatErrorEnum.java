package com.astrsomn.api.runtime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.IError;

/**
 * Astro Chat Error Enum
 */
@Getter
@AllArgsConstructor
public enum AstroChatErrorEnum implements IError {
    /**
     * Chat not found
     */
    CHAT_NOT_FOUND(20001, "Chat not found"),
    
    /**
     * Chat creation failed
     */
    CHAT_CREATE_FAILED(20002, "Chat creation failed"),
    
    /**
     * Chat update failed
     */
    CHAT_UPDATE_FAILED(20003, "Chat update failed"),
    
    /**
     * Chat deletion failed
     */
    CHAT_DELETE_FAILED(20004, "Chat deletion failed"),
    
    /**
     * Chat parameter error
     */
    CHAT_PARAM_ERROR(20005, "Chat parameter error"),
    
    /**
     * Chat permission denied
     */
    CHAT_PERMISSION_DENIED(20006, "Chat permission denied");
    
    private final int code;
    private final String message;
}