package com.astrsomn.server.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

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

        // multipart 不能先读入缓存：wrapper 会耗尽底层 InputStream，而 Servlet 规范里 getParts() 仍委托底层请求，
        // 会导致 multipartFiles 为空（体内容只留在 CachedBody 里，与 Spring 解析路径不一致）。
        String contentType = httpRequest.getContentType();
        if (contentType != null && contentType.toLowerCase(Locale.ROOT).startsWith("multipart/")) {
            chain.doFilter(request, response);
            return;
        }

        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpRequest);
        chain.doFilter(wrappedRequest, response);
    }

    @Override
    public void destroy() {
        log.info("RequestWrapperFilter 销毁");
    }
}
