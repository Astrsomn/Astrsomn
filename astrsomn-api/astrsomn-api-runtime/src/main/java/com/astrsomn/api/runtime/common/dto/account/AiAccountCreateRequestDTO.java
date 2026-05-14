package com.astrsomn.api.runtime.common.dto.account;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiAccountCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String accountKey;
    private String accountName;
    private String provider;
    private String apiUrl;
    private String apiKey;
    private String apiSecret;
    private Long accountTokens;
    private String status;
}