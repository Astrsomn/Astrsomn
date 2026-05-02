package com.astrsomn.api.workflow.domain.dto.instance;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowInstanceCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long deploymentId;
    private String businessKey;
    private String executionStatus;
    private String currentNodeId;
    private String stateJson;
}
