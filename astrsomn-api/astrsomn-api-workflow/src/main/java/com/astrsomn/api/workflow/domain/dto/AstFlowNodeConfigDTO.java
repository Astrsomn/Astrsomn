package com.astrsomn.api.workflow.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowNodeConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long flowDefinitionId;
    private String nodeId;
    private String nodeType;
    private String configJson;
}
