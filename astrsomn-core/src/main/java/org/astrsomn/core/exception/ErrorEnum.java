package org.astrsomn.core.exception;

/**
 * 错误枚举
 */
public enum ErrorEnum {
    /**
     * 系统错误
     */
    SYSTEM_ERROR(500, "系统内部错误"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未找到资源
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 权限不足
     */
    PERMISSION_DENIED(403, "权限不足"),

    /**
     * 业务逻辑错误
     */
    BUSINESS_ERROR(400, "业务逻辑错误"),

    /**
     * 网络错误
     */
    NETWORK_ERROR(503, "网络错误"),

    /**
     * 数据库错误
     */
    DATABASE_ERROR(500, "数据库错误");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final String message;

    ErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
