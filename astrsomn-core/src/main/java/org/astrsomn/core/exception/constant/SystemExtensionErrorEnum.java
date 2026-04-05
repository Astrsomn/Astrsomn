package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * 系统扩展错误枚举
 */
@Getter
@AllArgsConstructor
public enum SystemExtensionErrorEnum implements IError {
    /**
     * 扩展不存在
     */
    EXTENSION_NOT_FOUND(190001, "扩展不存在"),
    
    /**
     * 扩展创建失败
     */
    EXTENSION_CREATE_FAILED(190002, "扩展创建失败"),
    
    /**
     * 扩展更新失败
     */
    EXTENSION_UPDATE_FAILED(190003, "扩展更新失败"),
    
    /**
     * 扩展删除失败
     */
    EXTENSION_DELETE_FAILED(190004, "扩展删除失败"),
    
    /**
     * 扩展参数错误
     */
    EXTENSION_PARAM_ERROR(190005, "扩展参数错误"),
    
    /**
     * 扩展权限不足
     */
    EXTENSION_PERMISSION_DENIED(190006, "扩展权限不足");
    
    private final int code;
    private final String message;
}