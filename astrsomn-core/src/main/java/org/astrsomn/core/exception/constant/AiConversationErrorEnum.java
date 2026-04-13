package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI Conversation Error Enum
 */
@Getter
@AllArgsConstructor
public enum AiConversationErrorEnum implements IError {
    /**
     * Conversation not found
     */
    CONVERSATION_NOT_FOUND(30001, "Conversation not found"),
    
    /**
     * Conversation creation failed
     */
    CONVERSATION_CREATE_FAILED(30002, "Conversation creation failed"),
    
    /**
     * Conversation update failed
     */
    CONVERSATION_UPDATE_FAILED(30003, "Conversation update failed"),
    
    /**
     * Conversation deletion failed
     */
    CONVERSATION_DELETE_FAILED(30004, "Conversation deletion failed"),
    
    /**
     * Conversation parameter error
     */
    CONVERSATION_PARAM_ERROR(30005, "Conversation parameter error"),
    
    /**
     * Conversation permission denied
     */
    CONVERSATION_PERMISSION_DENIED(30006, "Conversation permission denied");
    
    private final int code;
    private final String message;
}