package com.astrsomn.api.workflow.domain.dto.bizidempotent;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowBizIdempotentUpdateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String idempotentKey;
    private String bizType;
    private String bizId;
    private String requestHash;
    private String resultRef;
    private Long expireAtMs;
}
