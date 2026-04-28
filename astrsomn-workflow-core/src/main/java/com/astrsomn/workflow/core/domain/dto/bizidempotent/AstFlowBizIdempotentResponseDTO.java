package com.astrsomn.workflow.core.domain.dto.bizidempotent;

import com.astrsomn.commn.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowBizIdempotentResponseDTO extends BaseEntity<Long> {

    private String idempotentKey;
    private String bizType;
    private String bizId;
    private String requestHash;
    private String resultRef;
    private Long expireAtMs;
}
