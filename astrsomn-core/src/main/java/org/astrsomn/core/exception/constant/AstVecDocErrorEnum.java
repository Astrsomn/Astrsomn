package org.astrsomn.core.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.exception.base.IError;

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
    DOC_PERMISSION_DENIED(130006, "Document permission denied");
    
    private final int code;
    private final String message;
}