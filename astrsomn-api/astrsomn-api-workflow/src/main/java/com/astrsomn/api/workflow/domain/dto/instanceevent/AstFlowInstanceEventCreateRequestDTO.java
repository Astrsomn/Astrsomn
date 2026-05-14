package com.astrsomn.api.workflow.domain.dto.instanceevent;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowInstanceEventCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long instanceId;
    private String eventType;
    private String nodeId;
    private Long eventTimeMs;
    private String eventDataJson;
    private String traceId;
}