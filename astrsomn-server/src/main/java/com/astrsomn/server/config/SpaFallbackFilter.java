package com.astrsomn.server.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SpaFallbackFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        if ("GET".equalsIgnoreCase(method) && shouldForwardToIndex(uri)) {
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldForwardToIndex(String uri) {
        if (uri == null || uri.isBlank() || "/".equals(uri)) {
            return true;
        }
        if ("/api".equals(uri) || uri.startsWith("/api/")) {
            return false;
        }
        if ("/v1/astro".equals(uri) || uri.startsWith("/v1/astro/")) {
            return false;
        }
        if (uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs") || uri.startsWith("/webjars/")) {
            return false;
        }
        if ("/doc.html".equals(uri)) {
            return false;
        }
        // Requests with extension should be served by static resource chain directly.
        return !uri.contains(".");
    }
}
