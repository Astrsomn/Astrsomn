package com.astrsomn.workflow.core.domain.dto.definition;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowDefinitionCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String workflowKey;
    private String workflowName;
    private String description;
    private String graphJson;
}
