package com.astrsomn.api.workflow.domain.dto.instanceevent;

import com.astrsomn.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowInstanceEventQueryRequestDTO extends BaseEntity<Long> {

    private Long instanceId;
    private String eventType;
    private String nodeId;
    private String traceId;
}