package com.astrsomn.workflow.core.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowInstanceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long deploymentId;
    private String businessKey;
    private String executionStatus;
    private String currentNodeId;
    private String stateJson;
}
