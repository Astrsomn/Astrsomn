package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * 向量片段错误枚举
 */
@Getter
@AllArgsConstructor
public enum AstVecSegmentErrorEnum implements IError {
    /**
     * 片段不存在
     */
    SEGMENT_NOT_FOUND(140001, "片段不存在"),
    
    /**
     * 片段创建失败
     */
    SEGMENT_CREATE_FAILED(140002, "片段创建失败"),
    
    /**
     * 片段更新失败
     */
    SEGMENT_UPDATE_FAILED(140003, "片段更新失败"),
    
    /**
     * 片段删除失败
     */
    SEGMENT_DELETE_FAILED(140004, "片段删除失败"),
    
    /**
     * 片段参数错误
     */
    SEGMENT_PARAM_ERROR(140005, "片段参数错误"),
    
    /**
     * 片段权限不足
     */
    SEGMENT_PERMISSION_DENIED(140006, "片段权限不足");
    
    private final int code;
    private final String message;
}