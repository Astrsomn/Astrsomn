package com.astrsomn.api.workflow.domain.dto.instanceevent;

import com.astrsomn.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowInstanceEventResponseDTO extends BaseEntity<Long> {

    private Long instanceId;
    private String eventType;
    private String nodeId;
    private Long eventTimeMs;
    private String eventDataJson;
    private String traceId;
}