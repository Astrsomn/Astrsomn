package com.astrsomn.api.workflow.domain.dto.msgoutbox;

import com.astrsomn.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowMsgOutboxQueryRequestDTO extends BaseEntity<Long> {

    private String bizType;
    private String bizId;
    private String msgStatus;
    private String idempotentKey;
}