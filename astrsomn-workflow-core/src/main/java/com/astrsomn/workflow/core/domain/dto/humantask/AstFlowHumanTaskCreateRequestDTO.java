package com.astrsomn.workflow.core.domain.dto.humantask;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowHumanTaskCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long instanceId;
    private String nodeId;
    private String taskStatus;
    private String payload;
    private String actionData;
}
