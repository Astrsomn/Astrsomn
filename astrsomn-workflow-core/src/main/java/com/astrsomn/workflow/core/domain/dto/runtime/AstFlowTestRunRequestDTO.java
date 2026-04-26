package com.astrsomn.workflow.core.domain.dto.runtime;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowTestRunRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String workflowKey;
    private String userMessage;
    private String memoryKey;
}
