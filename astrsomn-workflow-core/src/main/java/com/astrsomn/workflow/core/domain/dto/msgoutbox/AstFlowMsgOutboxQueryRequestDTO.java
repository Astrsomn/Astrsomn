package com.astrsomn.workflow.core.domain.dto.msgoutbox;

import com.astrsomn.commn.base.BaseEntity;
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
