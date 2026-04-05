package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * 系统配置错误枚举
 */
@Getter
@AllArgsConstructor
public enum SystemConfigErrorEnum implements IError {
    /**
     * 配置不存在
     */
    CONFIG_NOT_FOUND(170001, "配置不存在"),
    
    /**
     * 配置创建失败
     */
    CONFIG_CREATE_FAILED(170002, "配置创建失败"),
    
    /**
     * 配置更新失败
     */
    CONFIG_UPDATE_FAILED(170003, "配置更新失败"),
    
    /**
     * 配置删除失败
     */
    CONFIG_DELETE_FAILED(170004, "配置删除失败"),
    
    /**
     * 配置参数错误
     */
    CONFIG_PARAM_ERROR(170005, "配置参数错误"),
    
    /**
     * 配置权限不足
     */
    CONFIG_PERMISSION_DENIED(170006, "配置权限不足");
    
    private final int code;
    private final String message;
}