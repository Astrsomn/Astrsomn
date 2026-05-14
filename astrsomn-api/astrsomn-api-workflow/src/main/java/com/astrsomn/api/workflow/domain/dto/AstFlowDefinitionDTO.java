package com.astrsomn.api.workflow.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowDefinitionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String flowKey;
    private String category;
    private String draftGraphJson;
}