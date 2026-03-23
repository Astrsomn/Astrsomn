package org.astrsomn.server.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // TODO: 实现权限校验逻辑
        
        // 1. 获取当前用户信息
        // Long userId = UserContext.getUserId();
        // if (userId == null) {
        //     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //     return false;
        // }
        
        // 2. 获取请求的权限标识
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        // 3. 检查用户是否有权限访问该接口
        // if (!hasPermission(userId, uri, method)) {
        //     response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        //     response.setContentType("application/json;charset=UTF-8");
        //     response.getWriter().write("{\"code\":403,\"message\":\"无权访问\"}");
        //     return false;
        // }
        
        log.info("权限校验通过 - URI: {}, Method: {}", uri, method);
        return true;
    }

    private boolean hasPermission(Long userId, String uri, String method) {
        // TODO: 实现权限校验逻辑
        // 1. 从数据库或缓存中获取用户权限列表
        // 2. 检查当前URI和方法是否在权限列表中
        // 3. 支持通配符匹配（如 /api/agent/**）
        return true;
    }
}
