package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI MCP错误枚举
 */
@Getter
@AllArgsConstructor
public enum AiMcpErrorEnum implements IError {
    /**
     * MCP不存在
     */
    MCP_NOT_FOUND(50001, "MCP不存在"),
    
    /**
     * MCP创建失败
     */
    MCP_CREATE_FAILED(50002, "MCP创建失败"),
    
    /**
     * MCP更新失败
     */
    MCP_UPDATE_FAILED(50003, "MCP更新失败"),
    
    /**
     * MCP删除失败
     */
    MCP_DELETE_FAILED(50004, "MCP删除失败"),
    
    /**
     * MCP参数错误
     */
    MCP_PARAM_ERROR(50005, "MCP参数错误"),
    
    /**
     * MCP权限不足
     */
    MCP_PERMISSION_DENIED(50006, "MCP权限不足");
    
    private final int code;
    private final String message;
}