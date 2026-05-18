package com.astrsomn.starter.runtime.langchain.exception;


public class NoAvailableEndpointException extends AstroRouteException {

    public NoAvailableEndpointException() {
        super(ErrorCode.NO_AVAILABLE_ENDPOINT);
    }

    public NoAvailableEndpointException(String detail) {
        super(ErrorCode.NO_AVAILABLE_ENDPOINT, detail);
    }

    public NoAvailableEndpointException(String detail, String agentKey) {
        super(ErrorCode.NO_AVAILABLE_ENDPOINT, detail,
                context().put("agentKey", agentKey).build());
    }
}
