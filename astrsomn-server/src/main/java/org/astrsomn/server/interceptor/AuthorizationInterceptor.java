package org.astrsomn.server.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.SystemUserEnum;
import org.astrsomn.core.common.constant.SystemUserEnum.AdminEnum;
import org.astrsomn.server.util.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    private static final String ADMIN_FLAG_KEY = "adminFlag";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        Long userId = UserContext.getUserId();
        if (userId == null) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, 401, "未授权访问，请先登录");
            return false;
        }

        Object adminFlagObj = UserContext.get(ADMIN_FLAG_KEY);
        String adminFlag = adminFlagObj != null ? String.valueOf(adminFlagObj) : null;
        if (!isAdmin(adminFlag)) {
            writeJsonError(response, HttpServletResponse.SC_FORBIDDEN, 403, "无权访问");
            return false;
        }

        log.info("权限校验通过 - URI: {}, Method: {}, UserId: {}", uri, method, userId);
        return true;
    }

    private boolean isAdmin(String adminFlag) {
        if (adminFlag == null) {
            return false;
        }
        AdminEnum adminEnum = parseAdminEnum(adminFlag);
        return AdminEnum.YES.equals(adminEnum);
    }

    private AdminEnum parseAdminEnum(String adminFlag) {
        for (AdminEnum e : AdminEnum.values()) {
            if (e.getCode().equalsIgnoreCase(adminFlag)) {
                return e;
            }
        }
        return null;
    }

    private void writeJsonError(HttpServletResponse response, int httpStatus, int code, String message) {
        response.setStatus(httpStatus);
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + escapeJson(message) + "\"}");
        } catch (Exception e) {
            log.error("写入权限错误响应失败: {}", e.getMessage(), e);
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
