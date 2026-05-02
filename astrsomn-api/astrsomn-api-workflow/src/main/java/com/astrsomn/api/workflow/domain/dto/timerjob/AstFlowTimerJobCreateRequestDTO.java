package com.astrsomn.api.workflow.domain.dto.timerjob;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowTimerJobCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long instanceId;
    private String nodeId;
    private String jobType;
    private Long dueTimeMs;
    private String jobStatus;
    private Integer retryCount;
    private Integer maxRetry;
    private String lastError;
    private String payloadJson;
}
