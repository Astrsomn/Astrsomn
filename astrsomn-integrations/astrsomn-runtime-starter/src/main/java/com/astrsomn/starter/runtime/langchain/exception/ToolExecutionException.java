package com.astrsomn.starter.runtime.langchain.exception;


public class ToolExecutionException extends AstroToolException {

    public ToolExecutionException(String detail, Throwable cause) {
        super(ErrorCode.TOOL_EXECUTION_FAILED, detail, cause);
    }
}
