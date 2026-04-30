package com.astrsomn.workflow.core.domain.dto.deployment;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowDeploymentCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long flowDefinitionId;
    private Integer version;
    private String deployedGraphJson;
    private Boolean latest;
}
