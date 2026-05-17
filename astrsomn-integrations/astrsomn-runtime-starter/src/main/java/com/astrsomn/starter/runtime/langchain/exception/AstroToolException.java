package com.astrsomn.starter.runtime.langchain.exception;

import java.util.Map;

/**
 * 工具调用相关异常基类。
 */
public class AstroToolException extends AstroException {

    public AstroToolException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public AstroToolException(ErrorCode errorCode, String detail, Throwable cause) {
        super(errorCode, detail, cause);
    }

    public AstroToolException(ErrorCode errorCode, String detail, Map<String, Object> context) {
        super(errorCode, detail, context);
    }

    public AstroToolException(ErrorCode errorCode, String detail, Map<String, Object> context, Throwable cause) {
        super(errorCode, detail, context, cause);
    }
}
