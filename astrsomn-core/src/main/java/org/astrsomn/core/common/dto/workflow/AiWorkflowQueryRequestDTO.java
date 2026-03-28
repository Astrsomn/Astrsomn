package org.astrsomn.core.common.dto.workflow;

import lombok.Data;

@Data
public class AiWorkflowQueryRequestDTO {

    private String workflowKey;

    private String workflowName;

    private String status;
}
