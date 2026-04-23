package com.astrsomn.core.exception.base;

/**
 * 错误接口，定义错误码和消息的获取方法
 */
public interface IError {
    /**
     * 获取错误码
     * @return 错误码
     */
    int getCode();
    
    /**
     * 获取错误消息
     * @return 错误消息
     */
    String getMessage();
}