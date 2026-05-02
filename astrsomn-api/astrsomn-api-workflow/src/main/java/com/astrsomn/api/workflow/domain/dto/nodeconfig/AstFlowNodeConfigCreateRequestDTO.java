package com.astrsomn.api.workflow.domain.dto.nodeconfig;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowNodeConfigCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long flowDefinitionId;
    private String nodeId;
    private String nodeType;
    private String configJson;
}
