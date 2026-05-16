package com.astrsomn.starter.runtime.langchain.exception;

import java.util.Map;

/**
 * 故障转移尝试次数耗尽，所有端点均调用失败。
 */
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
