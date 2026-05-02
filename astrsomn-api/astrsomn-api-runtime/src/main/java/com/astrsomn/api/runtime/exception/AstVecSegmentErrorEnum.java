package com.astrsomn.api.runtime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.IError;

/**
 * Vector Segment Error Enum
 */
@Getter
@AllArgsConstructor
public enum AstVecSegmentErrorEnum implements IError {
    /**
     * Segment not found
     */
    SEGMENT_NOT_FOUND(140001, "Segment not found"),
    
    /**
     * Segment creation failed
     */
    SEGMENT_CREATE_FAILED(140002, "Segment creation failed"),
    
    /**
     * Segment update failed
     */
    SEGMENT_UPDATE_FAILED(140003, "Segment update failed"),
    
    /**
     * Segment deletion failed
     */
    SEGMENT_DELETE_FAILED(140004, "Segment deletion failed"),
    
    /**
     * Segment parameter error
     */
    SEGMENT_PARAM_ERROR(140005, "Segment parameter error"),
    
    /**
     * Segment permission denied
     */
    SEGMENT_PERMISSION_DENIED(140006, "Segment permission denied");
    
    private final int code;
    private final String message;
}