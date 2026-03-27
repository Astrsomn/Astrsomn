package org.astrsomn.server.api;


import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.server.dto.request.LoginRequest;
import org.astrsomn.server.dto.request.RefreshTokenRequest;
import org.astrsomn.server.dto.response.LoginResponse;
import org.astrsomn.server.service.AuthService;
import org.astrsomn.server.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j

@RestController
@RequestMapping("/v1/astro/auth")
public class AstroAuthController extends BaseController {

    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public BaseResponse<LoginResponse> login( @RequestBody LoginRequest request) {
        log.info("收到登录请求 - Username: {}", request.getUsername());
        LoginResponse response = authService.login(request);
        return success(response);
    }


    @PostMapping("/refresh-token")
    public BaseResponse<String> refreshToken( @RequestBody RefreshTokenRequest request) {
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
}
