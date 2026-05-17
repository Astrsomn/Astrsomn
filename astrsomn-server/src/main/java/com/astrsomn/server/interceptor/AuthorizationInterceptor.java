package com.astrsomn.server.interceptor;

import com.astrsomn.system.constant.SystemUserEnum.UserRoleEnum;
import com.astrsomn.server.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 基于 {@link UserRoleEnum} 的接口访问控制。
 * <ul>
 *   <li>超级管理员：用户管理、环境管理、全部 AI 配置接口。</li>
 *   <li>环境管理员：仅 AI 相关配置接口（同一环境内数据范围由业务层 envCode 约束）。</li>
 *   <li>普通用户：不可访问本拦截器保护的后台配置接口。</li>
 * </ul>
 */
@Slf4j
@Component
public class AuthorizationInterceptor extends AbstractSecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String path = uri.contains("?") ? uri.substring(0, uri.indexOf('?')) : uri;

        Long userId = UserContext.getUserId();
        if (userId == null) {
            return writeUnauthorized(response, "未授权访问，请先登录");
        }

        Object roleObj = UserContext.get("userRole");
        String roleCode = roleObj != null ? String.valueOf(roleObj) : null;
        UserRoleEnum role = UserRoleEnum.fromCode(roleCode);

        if (isPlatformAdminPath(path)) {
            if (!UserRoleEnum.canManagePlatformUsers(role.getCode())) {
                return writeForbidden(response, "需要超级管理员权限");
            }
            log.info("权限校验通过(平台管理) - URI: {}, UserId: {}", path, userId);
            return true;
        }

        if (isAiConfigPath(path)) {
            if (!UserRoleEnum.canManageAiConfig(role.getCode())) {
                return writeForbidden(response, "需要管理员或环境管理员权限");
            }
            log.info("权限校验通过(AI配置) - URI: {}, UserId: {}, role={}", path, userId, role.getCode());
            return true;
        }

        return true;
    }

    /**
     * 用户 / 环境主数据，仅超级管理员
     */
    private boolean isPlatformAdminPath(String path) {
        return path.startsWith("/v1/astro/system-user") || path.startsWith("/v1/astro/system-env");
    }

    /**
     * AI 配置类接口
     */
    private boolean isAiConfigPath(String path) {
        return path.startsWith("/v1/astro/ai-agent")
                || path.startsWith("/v1/astro/ai-model")
                || path.startsWith("/v1/astro/ai-tool")
                || path.startsWith("/v1/astro/ai-mcp")
                || path.startsWith("/v1/astro/ai-chat-message")
                || path.startsWith("/v1/astro/ai-chat-session")
                || path.startsWith("/v1/astro/ai-prompt")
                || path.startsWith("/v1/astro/ai-template")
                || path.startsWith("/v1/astro/ai-workflow")
                || path.startsWith("/v1/astro/ai-instance")
                || path.startsWith("/v1/astro/ai-account")
                || path.startsWith("/v1/astro/system-message")
                || path.startsWith("/v1/astro/sse");
    }

}
