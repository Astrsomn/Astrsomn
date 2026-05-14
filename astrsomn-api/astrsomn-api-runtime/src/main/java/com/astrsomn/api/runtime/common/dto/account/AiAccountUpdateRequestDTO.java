package com.astrsomn.api.runtime.common.dto.account;

import com.astrsomn.common.base.BaseEntity;
import com.astrsomn.api.runtime.common.entity.AiAccountEntity;
import lombok.Data;

import java.io.Serializable;


@Data
public class AiAccountUpdateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String accountKey;
    private String accountName;
    private String extensionCode;
    private String apiUrl;
    private String apiKey;
    private String apiSecret;
    private Long accountTokens;
    private String status;

}