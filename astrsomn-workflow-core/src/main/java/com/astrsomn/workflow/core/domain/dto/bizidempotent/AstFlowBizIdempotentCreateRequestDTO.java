package com.astrsomn.workflow.core.domain.dto.bizidempotent;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowBizIdempotentCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idempotentKey;
    private String bizType;
    private String bizId;
    private String requestHash;
    private String resultRef;
    private Long expireAtMs;
}
