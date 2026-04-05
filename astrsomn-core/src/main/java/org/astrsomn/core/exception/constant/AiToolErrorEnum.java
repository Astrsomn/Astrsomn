package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI工具错误枚举
 */
@Getter
@AllArgsConstructor
public enum AiToolErrorEnum implements IError {
    /**
     * 工具不存在
     */
    TOOL_NOT_FOUND(100001, "工具不存在"),
    
    /**
     * 工具创建失败
     */
    TOOL_CREATE_FAILED(100002, "工具创建失败"),
    
    /**
     * 工具更新失败
     */
    TOOL_UPDATE_FAILED(100003, "工具更新失败"),
    
    /**
     * 工具删除失败
     */
    TOOL_DELETE_FAILED(100004, "工具删除失败"),
    
    /**
     * 工具参数错误
     */
    TOOL_PARAM_ERROR(100005, "工具参数错误"),
    
    /**
     * 工具权限不足
     */
    TOOL_PERMISSION_DENIED(100006, "工具权限不足");
    
    private final int code;
    private final String message;
}