package com.astrsomn.server.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.core.common.entity.SystemUserEntity;
import com.astrsomn.core.mapper.SystemUserMapper;
import com.astrsomn.server.util.JwtUtil;
import com.astrsomn.server.util.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor extends AbstractSecurityInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final SystemUserMapper systemUserMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = extractToken(request);

        if (token == null || token.isEmpty()) {
            return writeUnauthorized(response, "未授权访问，请先登录");
        }

        if (!jwtUtil.validateToken(token)) {
            return writeUnauthorized(response, "Token无效或已过期");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);

        if (userId == null || username == null) {
            return writeUnauthorized(response, "Token解析失败");
        }

        SystemUserEntity user = systemUserMapper.selectOne(
                new LambdaQueryWrapper<SystemUserEntity>()
                        .eq(SystemUserEntity::getId, userId)
        );

        if (user == null) {
            return writeUnauthorized(response, "用户不存在");
        }

        UserContext.setUserId(user.getId());
        UserContext.setUsername(user.getUsername());
        UserContext.set("email", user.getEmail());
        UserContext.set("adminFlag", user.getAdminFlag());
        UserContext.set("userRole", user.getUserRole());
        UserContext.set("envCode", user.getEnvCode());
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
