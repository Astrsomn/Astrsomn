package com.astrsomn.starter.runtime.langchain.exception;

/**
 * 工具执行失败。
 */
public class ToolExecutionException extends AstroToolException {

    public ToolExecutionException(String detail, Throwable cause) {
        super(ErrorCode.TOOL_EXECUTION_FAILED, detail, cause);
    }
}
