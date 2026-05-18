package com.astrsomn.starter.runtime.langchain.exception;

import java.util.Map;


public class AllEndpointsFailedException extends AstroRouteException {

    public AllEndpointsFailedException(int attempts) {
        super(ErrorCode.ALL_ENDPOINTS_FAILED, "Failed after " + attempts + " attempts",
                context().put("attempts", attempts).build());
    }

    public AllEndpointsFailedException(int attempts, Throwable lastError) {
        super(ErrorCode.ALL_ENDPOINTS_FAILED, "Failed after " + attempts + " attempts",
                context().put("attempts", attempts).build(), lastError);
    }

    public AllEndpointsFailedException(int attempts, Throwable lastError, Map<String, Object> context) {
        super(ErrorCode.ALL_ENDPOINTS_FAILED, "Failed after " + attempts + " attempts", context, lastError);
    }
}
