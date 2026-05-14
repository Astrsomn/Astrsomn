package com.astrsomn.common.base;

import lombok.Getter;


@Getter
public class BusinessException extends RuntimeException {

    private final int code;


    private final IError error;


    public BusinessException(IError error) {
        super(error.getMessage());
        this.code = error.getCode();
        this.error = error;
    }


    public BusinessException(IError error, String message) {
        super(message);
        this.code = error.getCode();
        this.error = error;
    }


    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.error = null;
    }


    public BusinessException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.code = errorEnum.getCode();
        this.error = errorEnum;
    }


    public BusinessException(ErrorEnum errorEnum, String message) {
        super(message);
        this.code = errorEnum.getCode();
        this.error = errorEnum;
    }
}