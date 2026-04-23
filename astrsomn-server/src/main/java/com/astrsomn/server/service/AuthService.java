package com.astrsomn.server.service;

import com.astrsomn.core.common.dto.auth.LoginRequest;
import com.astrsomn.core.common.dto.auth.RefreshTokenRequest;
import com.astrsomn.core.common.dto.auth.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    String refreshToken(RefreshTokenRequest request);

    void logout(String token);
}
