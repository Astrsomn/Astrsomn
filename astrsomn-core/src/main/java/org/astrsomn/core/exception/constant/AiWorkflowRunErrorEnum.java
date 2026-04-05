package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI工作流运行错误枚举
 */
@Getter
@AllArgsConstructor
public enum AiWorkflowRunErrorEnum implements IError {
    /**
     * 工作流运行记录不存在
     */
    WORKFLOW_RUN_NOT_FOUND(120001, "工作流运行记录不存在"),
    
    /**
     * 工作流运行失败
     */
    WORKFLOW_RUN_FAILED(120002, "工作流运行失败"),
    
    /**
     * 工作流运行参数错误
     */
    WORKFLOW_RUN_PARAM_ERROR(120003, "工作流运行参数错误"),
    
    /**
     * 工作流运行权限不足
     */
    WORKFLOW_RUN_PERMISSION_DENIED(120004, "工作流运行权限不足");
    
    private final int code;
    private final String message;
}