package com.astrsomn.api.runtime.common.dto.account;

import com.astrsomn.common.base.BaseEntity;
import com.astrsomn.api.runtime.common.entity.AiAccountEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求；时间字段继承自 {@link AiAccountEntity} /
 * {@link BaseEntity}，勿在此重复声明非 ISO-8601 的日期格式。
 */
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
