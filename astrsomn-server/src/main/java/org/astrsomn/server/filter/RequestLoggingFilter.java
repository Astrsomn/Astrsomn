package org.astrsomn.server.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@WebFilter(urlPatterns = "/*", filterName = "requestLoggingFilter")
@Order(1)
public class RequestLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("RequestLoggingFilter 初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        long startTime = System.currentTimeMillis();
        
        // 记录请求信息
        log.info("请求开始 - URI: {}, Method: {}, IP: {}, User-Agent: {}",
                httpRequest.getRequestURI(),
                httpRequest.getMethod(),
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"));
        
        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("请求结束 - URI: {}, 耗时: {}ms", httpRequest.getRequestURI(), duration);
        }
    }

    @Override
    public void destroy() {
        log.info("RequestLoggingFilter 销毁");
    }

    private String getClientIp(HttpServletRequest request) {
        // TODO: 实现获取真实客户端IP的逻辑
        // 考虑代理服务器的情况，从 X-Forwarded-For 等头获取
        return request.getRemoteAddr();
    }
}
