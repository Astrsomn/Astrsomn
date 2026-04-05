package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * 系统环境错误枚举
 */
@Getter
@AllArgsConstructor
public enum SystemEnvErrorEnum implements IError {
    /**
     * 环境不存在
     */
    ENV_NOT_FOUND(180001, "环境不存在"),
    
    /**
     * 环境创建失败
     */
    ENV_CREATE_FAILED(180002, "环境创建失败"),
    
    /**
     * 环境更新失败
     */
    ENV_UPDATE_FAILED(180003, "环境更新失败"),
    
    /**
     * 环境删除失败
     */
    ENV_DELETE_FAILED(180004, "环境删除失败"),
    
    /**
     * 环境参数错误
     */
    ENV_PARAM_ERROR(180005, "环境参数错误"),
    
    /**
     * 环境权限不足
     */
    ENV_PERMISSION_DENIED(180006, "环境权限不足");
    
    private final int code;
    private final String message;
}