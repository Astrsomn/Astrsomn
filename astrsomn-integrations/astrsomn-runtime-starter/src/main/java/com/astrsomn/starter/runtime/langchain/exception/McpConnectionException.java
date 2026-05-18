package com.astrsomn.starter.runtime.langchain.exception;


public class McpConnectionException extends AstroToolException {

    public McpConnectionException(String detail, Throwable cause) {
        super(ErrorCode.MCP_CONNECTION_FAILED, detail, cause);
    }

    public McpConnectionException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
