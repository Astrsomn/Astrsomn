package com.astrsomn.api.workflow.domain.dto.runtime;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowNodeTraceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nodeId;
    private String nodeType;
    private String status;
    private Long durationMs;
    private Integer attemptNo;
    private String errorMessage;
}