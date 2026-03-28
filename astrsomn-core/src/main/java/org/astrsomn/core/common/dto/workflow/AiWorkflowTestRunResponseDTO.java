package org.astrsomn.core.common.dto.workflow;

import lombok.Data;

import java.util.Map;

@Data
public class AiWorkflowTestRunResponseDTO {

    private String status;

    private String lastNodeId;

    private String message;

    private Map<String, Object> variables;
}
