package com.astrsomn.server.service;

import com.astrsomn.system.dto.auth.LoginRequest;
import com.astrsomn.system.dto.auth.LoginResponse;
import com.astrsomn.system.dto.auth.RefreshTokenRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    String refreshToken(RefreshTokenRequest request);

    void logout(String token);
}
