package org.astrsomn.server.service;

import org.astrsomn.server.dto.request.LoginRequest;
import org.astrsomn.server.dto.request.RefreshTokenRequest;
import org.astrsomn.server.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    String refreshToken(RefreshTokenRequest request);

    void logout(String token);
}
