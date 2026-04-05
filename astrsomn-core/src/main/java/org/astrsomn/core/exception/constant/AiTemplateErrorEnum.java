package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI模板错误枚举
 */
@Getter
@AllArgsConstructor
public enum AiTemplateErrorEnum implements IError {
    /**
     * 模板不存在
     */
    TEMPLATE_NOT_FOUND(90001, "模板不存在"),
    
    /**
     * 模板创建失败
     */
    TEMPLATE_CREATE_FAILED(90002, "模板创建失败"),
    
    /**
     * 模板更新失败
     */
    TEMPLATE_UPDATE_FAILED(90003, "模板更新失败"),
    
    /**
     * 模板删除失败
     */
    TEMPLATE_DELETE_FAILED(90004, "模板删除失败"),
    
    /**
     * 模板参数错误
     */
    TEMPLATE_PARAM_ERROR(90005, "模板参数错误"),
    
    /**
     * 模板权限不足
     */
    TEMPLATE_PERMISSION_DENIED(90006, "模板权限不足");
    
    private final int code;
    private final String message;
}