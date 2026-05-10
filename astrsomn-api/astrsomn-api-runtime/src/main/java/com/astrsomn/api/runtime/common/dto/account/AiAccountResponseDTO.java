package com.astrsomn.api.runtime.common.dto.account;

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
//    private String apiKey;
//    private String apiSecret;
    private Long accountTokens;
    private Long callCount;
    private Long promptTokens;
    private Long completionTokens;
    private Long totalTokens;
    private Boolean accountKeyImmutable;
    private Long usedInstanceCount;
    private String usedInstanceKeys;
    private String usedInstanceNames;
    private String extensionName;
    private String providerAvatar;
    private LocalDateTime createTime;
    private String createUser;
    private String status;

}
