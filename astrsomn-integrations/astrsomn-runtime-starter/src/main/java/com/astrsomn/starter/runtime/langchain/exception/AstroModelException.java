package com.astrsomn.starter.runtime.langchain.exception;

import java.util.Map;

/**
 * 模型调用相关异常基类。
 */
public class AstroModelException extends AstroException {

    public AstroModelException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AstroModelException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public AstroModelException(ErrorCode errorCode, String detail, Throwable cause) {
        super(errorCode, detail, cause);
    }

    public AstroModelException(ErrorCode errorCode, String detail, Map<String, Object> context) {
        super(errorCode, detail, context);
    }

    public AstroModelException(ErrorCode errorCode, String detail, Map<String, Object> context, Throwable cause) {
        super(errorCode, detail, context, cause);
    }
}
