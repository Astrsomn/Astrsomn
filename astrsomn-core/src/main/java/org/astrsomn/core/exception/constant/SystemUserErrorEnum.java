package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * 系统用户错误枚举
 */
@Getter
@AllArgsConstructor
public enum SystemUserErrorEnum implements IError {
    /**
     * 用户不存在
     */
    USER_NOT_FOUND(200001, "用户不存在"),
    
    /**
     * 用户创建失败
     */
    USER_CREATE_FAILED(200002, "用户创建失败"),
    
    /**
     * 用户更新失败
     */
    USER_UPDATE_FAILED(200003, "用户更新失败"),
    
    /**
     * 用户删除失败
     */
    USER_DELETE_FAILED(200004, "用户删除失败"),
    
    /**
     * 用户参数错误
     */
    USER_PARAM_ERROR(200005, "用户参数错误"),
    
    /**
     * 用户权限不足
     */
    USER_PERMISSION_DENIED(200006, "用户权限不足");
    
    private final int code;
    private final String message;
}