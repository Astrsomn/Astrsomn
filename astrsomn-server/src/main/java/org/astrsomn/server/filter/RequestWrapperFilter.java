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
@WebFilter(urlPatterns = "/*", filterName = "requestWrapperFilter")
@Order(3)
public class RequestWrapperFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("RequestWrapperFilter 初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // 包装请求，支持多次读取请求体
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpRequest);
        
        chain.doFilter(wrappedRequest, response);
    }

    @Override
    public void destroy() {
        log.info("RequestWrapperFilter 销毁");
    }
}
