package com.astrsomn.commn.base;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础响应DTO
 * @param <T> 数据类型
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;

    private String message;

    private T data;

    public BaseResponse() {
    }

    public BaseResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public BaseResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static BaseResponse<?> success() {
        return new BaseResponse<>(true, "操作成功");
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, "操作成功", data);
    }


    public static <T> BaseResponse<T> fail(String message, T data) {
        return new BaseResponse<>(false, message, data);
    }

    public static <T> BaseResponse<T> fail(String message) {
        return new BaseResponse<>(false, message, null);
    }
}
