package com.astrsomn.server.api;


import com.astrsomn.system.constant.SystemUserEnum.UserRoleEnum;
import com.astrsomn.system.dto.auth.LoginRequest;
import com.astrsomn.system.dto.auth.LoginResponse;
import com.astrsomn.system.dto.auth.RefreshTokenRequest;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.server.service.AuthService;
import com.astrsomn.server.util.UserContext;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.context.EnvRuntime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j

@RestController
@RequestMapping("/v1/astro/auth")
public class AstroAuthController extends BaseController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AstrsomnProperties astrsomnProperties;


    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("收到登录请求 - Username: {}", request.getUsername());
        LoginResponse response = authService.login(request);
        return success(response);
    }


    @PostMapping("/refresh-token")
    public BaseResponse<String> refreshToken(@RequestBody RefreshTokenRequest request) {
        log.info("收到刷新Token请求");
        String newToken = authService.refreshToken(request);
        return success(newToken);
    }


    @PostMapping("/logout")
    public BaseResponse<String> logout(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        log.info("收到登出请求");
        authService.logout(token);
        UserContext.clear();
        return BaseResponse.success("");
    }


    @GetMapping("/current-user")
    public BaseResponse<LoginResponse> getCurrentUser() {
        Long userId = UserContext.getUserId();
        String username = UserContext.getUsername();
        Object email = UserContext.get("email");
        Object adminFlag = UserContext.get("adminFlag");
        Object userRole = UserContext.get("userRole");

        LoginResponse response = new LoginResponse();
        response.setUserId(userId);
        response.setUsername(username);
        if (email != null) {
            response.setEmail(String.valueOf(email));
        }
        if (adminFlag != null) {
            response.setAdminFlag(String.valueOf(adminFlag));
        }
        if (userRole != null) {
            response.setUserRole(String.valueOf(userRole));
        }

        return success(response);
    }

    /**
     * 当前请求生效的数据环境（与租户/写入填充一致）。超级管理员可通过请求头切换工作空间。
     */
    @GetMapping("/workspace-env")
    public BaseResponse<Map<String, Object>> workspaceEnv() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("effectiveEnvCode", EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties));
        String role = UserContext.getUserRole();
        m.put("canSwitchWorkspace", UserRoleEnum.canManagePlatformUsers(role));
        Object ue = UserContext.get("envCode");
        m.put("userEnvCode", ue != null ? String.valueOf(ue) : null);
        return success(m);
    }
}
