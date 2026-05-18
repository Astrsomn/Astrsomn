package com.astrsomn.starter.runtime.langchain.exception;

import java.util.Map;


public class AstroConfigException extends AstroException {

    public AstroConfigException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AstroConfigException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public AstroConfigException(ErrorCode errorCode, String detail, Map<String, Object> context) {
        super(errorCode, detail, context);
    }
}
