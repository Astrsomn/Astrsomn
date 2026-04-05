package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI敏感词错误枚举
 */
@Getter
@AllArgsConstructor
public enum AiSensitiveWordErrorEnum implements IError {
    /**
     * 敏感词不存在
     */
    SENSITIVE_WORD_NOT_FOUND(80001, "敏感词不存在"),
    
    /**
     * 敏感词创建失败
     */
    SENSITIVE_WORD_CREATE_FAILED(80002, "敏感词创建失败"),
    
    /**
     * 敏感词更新失败
     */
    SENSITIVE_WORD_UPDATE_FAILED(80003, "敏感词更新失败"),
    
    /**
     * 敏感词删除失败
     */
    SENSITIVE_WORD_DELETE_FAILED(80004, "敏感词删除失败"),
    
    /**
     * 敏感词参数错误
     */
    SENSITIVE_WORD_PARAM_ERROR(80005, "敏感词参数错误"),
    
    /**
     * 敏感词权限不足
     */
    SENSITIVE_WORD_PERMISSION_DENIED(80006, "敏感词权限不足");
    
    private final int code;
    private final String message;
}