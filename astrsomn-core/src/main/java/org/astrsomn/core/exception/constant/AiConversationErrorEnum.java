package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI会话错误枚举
 */
@Getter
@AllArgsConstructor
public enum AiConversationErrorEnum implements IError {
    /**
     * 会话不存在
     */
    CONVERSATION_NOT_FOUND(30001, "会话不存在"),
    
    /**
     * 会话创建失败
     */
    CONVERSATION_CREATE_FAILED(30002, "会话创建失败"),
    
    /**
     * 会话更新失败
     */
    CONVERSATION_UPDATE_FAILED(30003, "会话更新失败"),
    
    /**
     * 会话删除失败
     */
    CONVERSATION_DELETE_FAILED(30004, "会话删除失败"),
    
    /**
     * 会话参数错误
     */
    CONVERSATION_PARAM_ERROR(30005, "会话参数错误"),
    
    /**
     * 会话权限不足
     */
    CONVERSATION_PERMISSION_DENIED(30006, "会话权限不足");
    
    private final int code;
    private final String message;
}