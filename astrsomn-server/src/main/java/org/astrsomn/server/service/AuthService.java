package org.astrsomn.server.service;

import org.astrsomn.core.common.dto.auth.LoginRequest;
import org.astrsomn.core.common.dto.auth.RefreshTokenRequest;
import org.astrsomn.core.common.dto.auth.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    String refreshToken(RefreshTokenRequest request);

    void logout(String token);
}
