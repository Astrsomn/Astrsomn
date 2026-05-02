package com.astrsomn.api.workflow.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowDeploymentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long flowDefinitionId;
    private Integer version;
    private String deployedGraphJson;
    private Boolean latest;
}
