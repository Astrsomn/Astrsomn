package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.entity.SystemUserEntity;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.AuthErrorEnum;
import org.astrsomn.core.mapper.SystemUserMapper;
import org.astrsomn.server.dto.request.LoginRequest;
import org.astrsomn.server.dto.request.RefreshTokenRequest;
import org.astrsomn.server.dto.response.LoginResponse;
import org.astrsomn.server.service.AuthService;
import org.astrsomn.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        log.info("用户登录 - Username: {}", username);

        SystemUserEntity user = systemUserMapper.selectOne(
                new LambdaQueryWrapper<SystemUserEntity>()
                        .eq(SystemUserEntity::getUsername, username)
        );

        if (user == null) {
            log.warn("用户不存在 - Username: {}", username);
            throw new BusinessException(AuthErrorEnum.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("密码错误 - Username: {}", username);
            throw new BusinessException(AuthErrorEnum.PASSWORD_ERROR);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserRole());

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAdminFlag(user.getAdminFlag());
        response.setUserRole(user.getUserRole());
        response.setToken(token);
        response.setExpiresIn(expiration);

        log.info("用户登录成功 - Username: {}, UserId: {}", username, user.getId());

        return response;
    }

    @Override
    public String refreshToken(RefreshTokenRequest request) {
        String oldToken = request.getToken();

        log.info("刷新Token");

        if (!jwtUtil.validateToken(oldToken)) {
            throw new BusinessException(AuthErrorEnum.INVALID_TOKEN);
        }

        Long userId = jwtUtil.getUserIdFromToken(oldToken);
        String username = jwtUtil.getUsernameFromToken(oldToken);

        if (userId == null || username == null) {
            throw new BusinessException(AuthErrorEnum.INVALID_TOKEN);
        }

        SystemUserEntity user = systemUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(AuthErrorEnum.USER_NOT_FOUND);
        }

        String newToken = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserRole());

        log.info("Token刷新成功 - Username: {}", username);

        return newToken;
    }

    @Override
    public void logout(String token) {
        log.info("用户登出 - Token: {}", maskToken(token));

    }

    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}
