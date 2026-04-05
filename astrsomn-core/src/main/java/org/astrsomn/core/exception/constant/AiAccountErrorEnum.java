package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI账号错误枚举
 */
@Getter
@AllArgsConstructor
public enum AiAccountErrorEnum implements IError {
    /**
     * 账号不存在
     */
    ACCOUNT_NOT_FOUND(10001, "账号不存在"),
    
    /**
     * 账号创建失败
     */
    ACCOUNT_CREATE_FAILED(10002, "账号创建失败"),
    
    /**
     * 账号更新失败
     */
    ACCOUNT_UPDATE_FAILED(10003, "账号更新失败"),
    
    /**
     * 账号删除失败
     */
    ACCOUNT_DELETE_FAILED(10004, "账号删除失败"),
    
    /**
     * 账号参数错误
     */
    ACCOUNT_PARAM_ERROR(10005, "账号参数错误"),
    
    /**
     * 账号权限不足
     */
    ACCOUNT_PERMISSION_DENIED(10006, "账号权限不足");
    
    private final int code;
    private final String message;
}