package com.astrsomn.workflow.core.domain.dto.msgoutbox;

import com.astrsomn.commn.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowMsgOutboxResponseDTO extends BaseEntity<Long> {

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
