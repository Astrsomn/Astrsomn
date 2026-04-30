package com.astrsomn.server.interceptor;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 认证/鉴权拦截器公共响应能力。
 */
@Slf4j
public abstract class AbstractSecurityInterceptor {

    protected boolean writeUnauthorized(HttpServletResponse response, String message) {
        return writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, 401, message);
    }

    protected boolean writeForbidden(HttpServletResponse response, String message) {
        return writeJsonError(response, HttpServletResponse.SC_FORBIDDEN, 403, message);
    }

    protected boolean writeJsonError(HttpServletResponse response, int httpStatus, int code, String message) {
        response.setStatus(httpStatus);
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + escapeJson(message) + "\"}");
        } catch (IOException e) {
            log.error("写入安全拦截器错误响应失败: {}", e.getMessage(), e);
        }
        return false;
    }

    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
