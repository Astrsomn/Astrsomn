package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI代理错误枚举
 */
@Getter
@AllArgsConstructor
public enum AiAgentErrorEnum implements IError {
    /**
     * 代理不存在
     */
    AGENT_NOT_FOUND(20001, "代理不存在"),
    
    /**
     * 代理创建失败
     */
    AGENT_CREATE_FAILED(20002, "代理创建失败"),
    
    /**
     * 代理更新失败
     */
    AGENT_UPDATE_FAILED(20003, "代理更新失败"),
    
    /**
     * 代理删除失败
     */
    AGENT_DELETE_FAILED(20004, "代理删除失败"),
    
    /**
     * 代理参数错误
     */
    AGENT_PARAM_ERROR(20005, "代理参数错误"),
    
    /**
     * 代理权限不足
     */
    AGENT_PERMISSION_DENIED(20006, "代理权限不足");
    
    private final int code;
    private final String message;
}