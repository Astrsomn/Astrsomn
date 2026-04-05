package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI提示词错误枚举
 */
@Getter
@AllArgsConstructor
public enum AiPromptErrorEnum implements IError {
    /**
     * 提示词不存在
     */
    PROMPT_NOT_FOUND(70001, "提示词不存在"),
    
    /**
     * 提示词创建失败
     */
    PROMPT_CREATE_FAILED(70002, "提示词创建失败"),
    
    /**
     * 提示词更新失败
     */
    PROMPT_UPDATE_FAILED(70003, "提示词更新失败"),
    
    /**
     * 提示词删除失败
     */
    PROMPT_DELETE_FAILED(70004, "提示词删除失败"),
    
    /**
     * 提示词参数错误
     */
    PROMPT_PARAM_ERROR(70005, "提示词参数错误"),
    
    /**
     * 提示词权限不足
     */
    PROMPT_PERMISSION_DENIED(70006, "提示词权限不足");
    
    private final int code;
    private final String message;
}