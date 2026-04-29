package com.astrsomn.core.common.dto.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiAccountResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String envCode;
    private String accountKey;
    private String accountName;
    private String extensionCode;
    private String apiUrl;
    private String apiKey;
    private String apiSecret;
    private Long accountTokens;
    private Boolean accountKeyImmutable;
    private Long usedModelCount;
    private String usedModelKeys;
    private String usedModelNames;
    private String extensionName;
    private String providerAvatar;
    private LocalDateTime createTime;
    private String createUser;

}
