package com.astrsomn.api.workflow.domain.dto.definition;

import com.astrsomn.api.workflow.domain.entity.AstFlowDefinitionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowDefinitionResponseDTO extends AstFlowDefinitionEntity {



    private String workflowKey;
    private String workflowName;
    private Integer versionNo;
    private String description;
    private String graphJson;
    private String status;
}
