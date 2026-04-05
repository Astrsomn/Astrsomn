package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * 向量存储错误枚举
 */
@Getter
@AllArgsConstructor
public enum AstVecStoreErrorEnum implements IError {
    /**
     * 存储不存在
     */
    STORE_NOT_FOUND(160001, "存储不存在"),
    
    /**
     * 存储创建失败
     */
    STORE_CREATE_FAILED(160002, "存储创建失败"),
    
    /**
     * 存储更新失败
     */
    STORE_UPDATE_FAILED(160003, "存储更新失败"),
    
    /**
     * 存储删除失败
     */
    STORE_DELETE_FAILED(160004, "存储删除失败"),
    
    /**
     * 存储参数错误
     */
    STORE_PARAM_ERROR(160005, "存储参数错误"),
    
    /**
     * 存储权限不足
     */
    STORE_PERMISSION_DENIED(160006, "存储权限不足");
    
    private final int code;
    private final String message;
}