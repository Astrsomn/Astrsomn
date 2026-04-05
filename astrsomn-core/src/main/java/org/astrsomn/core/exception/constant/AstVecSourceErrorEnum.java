package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * 向量源错误枚举
 */
@Getter
@AllArgsConstructor
public enum AstVecSourceErrorEnum implements IError {
    /**
     * 源不存在
     */
    SOURCE_NOT_FOUND(150001, "源不存在"),
    
    /**
     * 源创建失败
     */
    SOURCE_CREATE_FAILED(150002, "源创建失败"),
    
    /**
     * 源更新失败
     */
    SOURCE_UPDATE_FAILED(150003, "源更新失败"),
    
    /**
     * 源删除失败
     */
    SOURCE_DELETE_FAILED(150004, "源删除失败"),
    
    /**
     * 源参数错误
     */
    SOURCE_PARAM_ERROR(150005, "源参数错误"),
    
    /**
     * 源权限不足
     */
    SOURCE_PERMISSION_DENIED(150006, "源权限不足");
    
    private final int code;
    private final String message;
}