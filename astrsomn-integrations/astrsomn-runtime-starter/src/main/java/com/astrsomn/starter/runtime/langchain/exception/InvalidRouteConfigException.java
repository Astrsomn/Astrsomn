package com.astrsomn.starter.runtime.langchain.exception;

import java.util.Map;

/**
 * 路由配置非法（如缺少必要的 key、端点列表为空等）。
 */
public class InvalidRouteConfigException extends AstroConfigException {

    public InvalidRouteConfigException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public InvalidRouteConfigException(ErrorCode errorCode, String detail, Map<String, Object> context) {
        super(errorCode, detail, context);
    }
}
