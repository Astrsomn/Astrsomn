package com.astrsomn.starter.runtime.langchain.exception;

import java.util.Map;


public class AstroRouteException extends AstroException {

    public AstroRouteException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AstroRouteException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public AstroRouteException(ErrorCode errorCode, String detail, Map<String, Object> context) {
        super(errorCode, detail, context);
    }

    public AstroRouteException(ErrorCode errorCode, String detail, Map<String, Object> context, Throwable cause) {
        super(errorCode, detail, context, cause);
    }
}
