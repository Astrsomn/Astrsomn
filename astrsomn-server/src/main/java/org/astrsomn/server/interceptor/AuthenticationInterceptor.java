package org.astrsomn.server.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.entity.SystemUserEntity;
import org.astrsomn.core.mapper.SystemUserMapper;
import org.astrsomn.server.exception.BusinessException;
import org.astrsomn.server.util.JwtUtil;
import org.astrsomn.server.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = extractToken(request);
        
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未授权访问，请先登录\"}");
            return false;
        }
        
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效或已过期\"}");
            return false;
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        
        if (userId == null || username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token解析失败\"}");
            return false;
        }
        
        SystemUserEntity user = systemUserMapper.selectOne(
                new LambdaQueryWrapper<SystemUserEntity>()
                        .eq(SystemUserEntity::getId, userId)
        );
        
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"用户不存在\"}");
            return false;
        }
        
        UserContext.setUserId(user.getId());
        UserContext.setUsername(user.getUsername());
        UserContext.set("email", user.getEmail());
        UserContext.set("adminFlag", user.getAdminFlag());
        UserContext.set("userRole", user.getUserRole());
        UserContext.setToken(token);
        UserContext.setClientIp(request.getRemoteAddr());
        
        log.info("用户认证成功 - URI: {}, Username: {}, UserId: {}", 
                request.getRequestURI(), username, userId);
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        String token = request.getParameter("token");
        if (token != null && !token.isEmpty()) {
            return token;
        }
        
        return null;
    }
}
