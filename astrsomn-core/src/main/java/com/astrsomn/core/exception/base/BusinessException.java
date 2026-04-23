package com.astrsomn.core.exception.base;

import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {
    /**
     * 错误码
     */
    private final int code;
    
    /**
     * 错误对象
     */
    private final IError error;

    /**
     * 构造方法
     * @param error 错误对象
     */
    public BusinessException(IError error) {
        super(error.getMessage());
        this.code = error.getCode();
        this.error = error;
    }

    /**
     * 构造方法
     * @param error 错误对象
     * @param message 错误消息
     */
    public BusinessException(IError error, String message) {
        super(message);
        this.code = error.getCode();
        this.error = error;
    }

    /**
     * 构造方法
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.error = null;
    }

    /**
     * 构造方法
     * @param errorEnum 错误枚举
     */
    public BusinessException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.code = errorEnum.getCode();
        this.error = errorEnum;
    }

    /**
     * 构造方法
     * @param errorEnum 错误枚举
     * @param message 错误消息
     */
    public BusinessException(ErrorEnum errorEnum, String message) {
        super(message);
        this.code = errorEnum.getCode();
        this.error = errorEnum;
    }
}
