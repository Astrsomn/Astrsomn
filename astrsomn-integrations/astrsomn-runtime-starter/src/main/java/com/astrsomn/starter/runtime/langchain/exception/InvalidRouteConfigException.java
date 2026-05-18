package com.astrsomn.starter.runtime.langchain.exception;

import java.util.Map;


public class InvalidRouteConfigException extends AstroConfigException {

    public InvalidRouteConfigException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public InvalidRouteConfigException(ErrorCode errorCode, String detail, Map<String, Object> context) {
        super(errorCode, detail, context);
    }
}
