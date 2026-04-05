package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * AI工作流错误枚举
 */
@Getter
@AllArgsConstructor
public enum AiWorkflowErrorEnum implements IError {
    /**
     * 工作流不存在
     */
    WORKFLOW_NOT_FOUND(110001, "工作流不存在"),
    
    /**
     * 工作流创建失败
     */
    WORKFLOW_CREATE_FAILED(110002, "工作流创建失败"),
    
    /**
     * 工作流更新失败
     */
    WORKFLOW_UPDATE_FAILED(110003, "工作流更新失败"),
    
    /**
     * 工作流删除失败
     */
    WORKFLOW_DELETE_FAILED(110004, "工作流删除失败"),
    
    /**
     * 工作流参数错误
     */
    WORKFLOW_PARAM_ERROR(110005, "工作流参数错误"),
    
    /**
     * 工作流权限不足
     */
    WORKFLOW_PERMISSION_DENIED(110006, "工作流权限不足");
    
    private final int code;
    private final String message;
}