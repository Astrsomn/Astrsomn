package com.astrsomn.api.workflow.domain.dto.definition;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowDefinitionUpdateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String workflowKey;
    private String workflowName;
    private String description;
    private String graphJson;
}