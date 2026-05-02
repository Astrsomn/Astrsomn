package com.astrsomn.server.filter;

import com.astrsomn.common.base.BusinessException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Component
@WebFilter(urlPatterns = "/*", filterName = "exceptionHandlingFilter")
@Order(4)
public class ExceptionHandlingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("ExceptionHandlingFilter 初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            chain.doFilter(request, response);
        } catch (Throwable e) {
            log.error("请求处理异常 - URI: {}, 异常: {}", httpRequest.getRequestURI(), e.getMessage(), e);
            
            if (httpResponse.isCommitted()) {
                return;
            }

            int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            int code = 500;
            String message = "系统内部错误";

            if (e instanceof BusinessException be) {
                code = be.getCode();
                status = be.getCode();
                message = be.getMessage();
            } else if (e instanceof IllegalArgumentException iae) {
                status = HttpServletResponse.SC_BAD_REQUEST;
                code = 400;
                message = iae.getMessage() != null ? iae.getMessage() : "参数错误";
            }

            httpResponse.setStatus(status);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write(buildJsonBody(code, message, e, httpRequest));
        }
    }

    private String buildJsonBody(int code, String message, Throwable ex, HttpServletRequest request) {
        return "{\"code\":" + code +
                ",\"message\":\"" + escapeJson(message) + "\"" +
                ",\"success\":false" +
                ",\"path\":\"" + escapeJson(request.getRequestURI()) + "\"" +
                ",\"exception\":\"" + escapeJson(ex.getClass().getName()) + "\"" +
                ",\"timestamp\":\"" + Instant.now() + "\"" +
                ",\"rootCause\":\"" + escapeJson(resolveRootCause(ex)) + "\"}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String resolveRootCause(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current.getMessage();
    }

    @Override
    public void destroy() {
        log.info("ExceptionHandlingFilter 销毁");
    }
}
