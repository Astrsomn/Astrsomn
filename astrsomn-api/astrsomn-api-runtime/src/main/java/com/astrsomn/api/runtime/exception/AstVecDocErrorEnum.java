package com.astrsomn.api.runtime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.IError;

/**
 * Vector Document Error Enum
 */
@Getter
@AllArgsConstructor
public enum AstVecDocErrorEnum implements IError {
    /**
     * Document not found
     */
    DOC_NOT_FOUND(130001, "Document not found"),
    
    /**
     * Document creation failed
     */
    DOC_CREATE_FAILED(130002, "Document creation failed"),
    
    /**
     * Document update failed
     */
    DOC_UPDATE_FAILED(130003, "Document update failed"),
    
    /**
     * Document deletion failed
     */
    DOC_DELETE_FAILED(130004, "Document deletion failed"),
    
    /**
     * Document parameter error
     */
    DOC_PARAM_ERROR(130005, "Document parameter error"),
    
    /**
     * Document permission denied
     */
    DOC_PERMISSION_DENIED(130006, "Document permission denied"),

    /**
     * 向量集合（Store）不存在
     */
    DOC_STORE_NOT_FOUND(130007, "Vector store not found"),

    /**
     * 向量源未就绪或未启用
     */
    DOC_SOURCE_NOT_READY(130008, "Vector source not ready"),

    /**
     * 向量化仅允许待处理状态
     */
    DOC_VECTORIZE_STATUS_INVALID(130009, "Document is not pending vectorization"),

    /**
     * 文件不存在或不可读
     */
    DOC_FILE_NOT_READABLE(130010, "Document file not readable"),

    /**
     * 文件内容不是支持的 UTF-8 文本
     */
    DOC_FILE_NOT_TEXT(130011, "Document file is not supported as UTF-8 text"),

    /**
     * 向量化执行失败
     */
    DOC_VECTORIZE_FAILED(130012, "Document vectorization failed");
    
    private final int code;
    private final String message;
}