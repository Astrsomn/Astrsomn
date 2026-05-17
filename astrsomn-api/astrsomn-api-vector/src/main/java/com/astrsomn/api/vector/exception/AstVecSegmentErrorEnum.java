package com.astrsomn.api.vector.exception;

import com.astrsomn.common.base.IError;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AstVecSegmentErrorEnum implements IError {

    SEGMENT_NOT_FOUND(140001, "Segment not found"),


    SEGMENT_CREATE_FAILED(140002, "Segment creation failed"),


    SEGMENT_UPDATE_FAILED(140003, "Segment update failed"),


    SEGMENT_DELETE_FAILED(140004, "Segment deletion failed"),


    SEGMENT_PARAM_ERROR(140005, "Segment parameter error"),


    SEGMENT_PERMISSION_DENIED(140006, "Segment permission denied");

    private final int code;
    private final String message;
}