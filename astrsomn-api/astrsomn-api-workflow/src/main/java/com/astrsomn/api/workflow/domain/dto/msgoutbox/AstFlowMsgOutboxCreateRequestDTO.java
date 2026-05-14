package com.astrsomn.api.workflow.domain.dto.msgoutbox;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowMsgOutboxCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bizType;
    private String bizId;
    private String topicOrEndpoint;
    private String payloadJson;
    private String msgStatus;
    private Integer retryCount;
    private Long nextRetryTimeMs;
    private String lastError;
    private String idempotentKey;
}