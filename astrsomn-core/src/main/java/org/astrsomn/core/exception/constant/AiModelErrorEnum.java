package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI模型错误枚举
 */
@Getter
@AllArgsConstructor
public enum AiModelErrorEnum implements IError {
    /**
     * 模型不存在
     */
    MODEL_NOT_FOUND(60001, "模型不存在"),
    
    /**
     * 模型创建失败
     */
    MODEL_CREATE_FAILED(60002, "模型创建失败"),
    
    /**
     * 模型更新失败
     */
    MODEL_UPDATE_FAILED(60003, "模型更新失败"),
    
    /**
     * 模型删除失败
     */
    MODEL_DELETE_FAILED(60004, "模型删除失败"),
    
    /**
     * 模型参数错误
     */
    MODEL_PARAM_ERROR(60005, "模型参数错误"),
    
    /**
     * 模型权限不足
     */
    MODEL_PERMISSION_DENIED(60006, "模型权限不足");
    
    private final int code;
    private final String message;
}