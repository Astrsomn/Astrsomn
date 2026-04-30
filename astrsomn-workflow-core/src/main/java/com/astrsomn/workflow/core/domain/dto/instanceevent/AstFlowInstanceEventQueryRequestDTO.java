package com.astrsomn.workflow.core.domain.dto.instanceevent;

import com.astrsomn.commn.base.BaseEntity;
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
