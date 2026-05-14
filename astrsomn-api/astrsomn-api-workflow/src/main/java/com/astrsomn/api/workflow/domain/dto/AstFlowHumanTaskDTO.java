package com.astrsomn.api.workflow.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowHumanTaskDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long instanceId;
    private String nodeId;
    private String taskStatus;
    private String payload;
    private String actionData;
}