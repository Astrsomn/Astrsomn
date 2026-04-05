package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI Prompt Error Enum
 */
@Getter
@AllArgsConstructor
public enum AiPromptErrorEnum implements IError {
    /**
     * Prompt not found
     */
    PROMPT_NOT_FOUND(70001, "Prompt not found"),
    
    /**
     * Prompt creation failed
     */
    PROMPT_CREATE_FAILED(70002, "Prompt creation failed"),
    
    /**
     * Prompt update failed
     */
    PROMPT_UPDATE_FAILED(70003, "Prompt update failed"),
    
    /**
     * Prompt deletion failed
     */
    PROMPT_DELETE_FAILED(70004, "Prompt deletion failed"),
    
    /**
     * Prompt parameter error
     */
    PROMPT_PARAM_ERROR(70005, "Prompt parameter error"),
    
    /**
     * Prompt permission denied
     */
    PROMPT_PERMISSION_DENIED(70006, "Prompt permission denied");
    
    private final int code;
    private final String message;
}