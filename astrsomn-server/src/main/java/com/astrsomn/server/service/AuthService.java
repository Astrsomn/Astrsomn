package com.astrsomn.server.service;

import com.astrsomn.api.runtime.common.dto.auth.LoginRequest;
import com.astrsomn.api.runtime.common.dto.auth.RefreshTokenRequest;
import com.astrsomn.api.runtime.common.dto.auth.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    String refreshToken(RefreshTokenRequest request);

    void logout(String token);
}
