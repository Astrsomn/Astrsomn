package com.astrsomn.api.runtime.common.dto.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String username;
    private String email;
    private String adminFlag;
    /** SUPER_ADMIN / ENV_ADMIN / USER */
    private String userRole;
    private String token;
    private Long expiresIn;
}