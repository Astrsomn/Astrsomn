package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI实例错误枚举
 */
@Getter
@AllArgsConstructor
public enum AiInstanceErrorEnum implements IError {
    /**
     * 实例不存在
     */
    INSTANCE_NOT_FOUND(40001, "实例不存在"),
    
    /**
     * 实例创建失败
     */
    INSTANCE_CREATE_FAILED(40002, "实例创建失败"),
    
    /**
     * 实例更新失败
     */
    INSTANCE_UPDATE_FAILED(40003, "实例更新失败"),
    
    /**
     * 实例删除失败
     */
    INSTANCE_DELETE_FAILED(40004, "实例删除失败"),
    
    /**
     * 实例参数错误
     */
    INSTANCE_PARAM_ERROR(40005, "实例参数错误"),
    
    /**
     * 实例权限不足
     */
    INSTANCE_PERMISSION_DENIED(40006, "实例权限不足");
    
    private final int code;
    private final String message;
}