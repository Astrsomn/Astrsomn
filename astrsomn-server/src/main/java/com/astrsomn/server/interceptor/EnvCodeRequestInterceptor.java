package com.astrsomn.server.interceptor;

import com.astrsomn.api.runtime.common.constant.SystemUserEnum.UserRoleEnum;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.util.UserContext;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.context.EnvScope;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 解析前端 {@value #HEADER_ENV_CODE}，写入 {@link EnvScope}。
 * <p>超级管理员可切换任意环境；其余角色固定为账号所属 {@code envCode}（与实例默认环境对齐）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EnvCodeRequestInterceptor implements HandlerInterceptor {

    public static final String HEADER_ENV_CODE = "X-Astrsomn-Env-Code";

    private final AstrsomnProperties astrsomnProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String defaultEnv = StringUtils.trimToNull(astrsomnProperties.getEnvCode());
        if (defaultEnv == null) {
            defaultEnv = "default";
        }

        Long userId = UserContext.getUserId();
        if (userId == null) {
            EnvScope.set(defaultEnv);
            return true;
        }

        String header = StringUtils.trimToNull(request.getHeader(HEADER_ENV_CODE));
        String roleCode = UserContext.getUserRole();
        boolean superAdmin = UserRoleEnum.canManagePlatformUsers(roleCode);

        Object envObj = UserContext.get("envCode");
        String userEnv = envObj == null ? null : StringUtils.trimToNull(String.valueOf(envObj));

        String effective;
        if (superAdmin) {
            effective = StringUtils.isNotBlank(header) ? header : defaultEnv;
        } else {
            effective = StringUtils.isNotBlank(userEnv) ? userEnv : defaultEnv;
            if (header != null && !header.equals(effective)) {
                log.debug("非超级管理员忽略环境请求头: userId={}, header={}, effective={}", userId, header, effective);
            }
        }

        EnvScope.set(effective);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        EnvScope.clear();
    }
}
