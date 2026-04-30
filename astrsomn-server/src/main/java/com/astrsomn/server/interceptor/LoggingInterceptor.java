package com.astrsomn.server.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("拦截器 - 请求开始 - URI: {}, Method: {}, Handler: {}",
                request.getRequestURI(),
                request.getMethod(),
                handler);
        
        // 记录请求开始时间到请求属性中
        request.setAttribute("startTime", System.currentTimeMillis());
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Object startTimeObj = request.getAttribute("startTime");
        long startTime = startTimeObj instanceof Long ? (Long) startTimeObj : System.currentTimeMillis();
        long duration = System.currentTimeMillis() - startTime;
        
        log.info("拦截器 - 请求完成 - URI: {}, 状态码: {}, 耗时: {}ms, 异常: {}",
                request.getRequestURI(),
                response.getStatus(),
                duration,
                ex != null ? ex.getMessage() : "无");
    }
}
