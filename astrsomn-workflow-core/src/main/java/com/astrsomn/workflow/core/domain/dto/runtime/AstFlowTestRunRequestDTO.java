package com.astrsomn.workflow.core.domain.dto.runtime;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class AstFlowTestRunRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String workflowKey;
    private String userMessage;
    private String memoryKey;
    private Map<String, Object> variables;
}
