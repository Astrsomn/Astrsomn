package com.astrsomn.api.workflow.domain.dto.bizidempotent;

import com.astrsomn.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowBizIdempotentQueryRequestDTO extends BaseEntity<Long> {

    private String idempotentKey;
    private String bizType;
    private String bizId;
}