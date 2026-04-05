package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

/**
 * 向量文档错误枚举
 */
@Getter
@AllArgsConstructor
public enum AstVecDocErrorEnum implements IError {
    /**
     * 文档不存在
     */
    DOC_NOT_FOUND(130001, "文档不存在"),
    
    /**
     * 文档创建失败
     */
    DOC_CREATE_FAILED(130002, "文档创建失败"),
    
    /**
     * 文档更新失败
     */
    DOC_UPDATE_FAILED(130003, "文档更新失败"),
    
    /**
     * 文档删除失败
     */
    DOC_DELETE_FAILED(130004, "文档删除失败"),
    
    /**
     * 文档参数错误
     */
    DOC_PARAM_ERROR(130005, "文档参数错误"),
    
    /**
     * 文档权限不足
     */
    DOC_PERMISSION_DENIED(130006, "文档权限不足");
    
    private final int code;
    private final String message;
}