package com.astrsomn.common.base;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorEnum implements IError {

    SYSTEM_ERROR(500, "系统内部错误"),


    PARAM_ERROR(400, "参数错误"),


    NOT_FOUND(404, "资源不存在"),


    PERMISSION_DENIED(403, "权限不足"),


    BUSINESS_ERROR(400, "业务逻辑错误"),


    NETWORK_ERROR(503, "网络错误"),


    DATABASE_ERROR(500, "数据库错误");


    private final int code;


    private final String message;
}