package org.astrsomn.server.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // TODO: 实现认证逻辑
        
        // 1. 获取Token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            // 从Cookie中获取
            // token = getCookieValue(request, "token");
        }
        
        // 2. 验证Token
        if (token == null || !validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未授权访问\"}");
            return false;
        }
        
        // 3. 解析用户信息并存储到上下文
        // UserContext.setUserId(parseUserId(token));
        // UserContext.setUsername(parseUsername(token));
        
        log.info("用户认证成功 - URI: {}, Token: {}", request.getRequestURI(), maskToken(token));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // TODO: 清理用户上下文
        // UserContext.clear();
    }

    private boolean validateToken(String token) {
        // TODO: 实现Token验证逻辑
        // 1. 检查Token格式
        // 2. 验证Token签名
        // 3. 检查Token是否过期
        // 4. 检查Token是否在黑名单中
        return true;
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}
