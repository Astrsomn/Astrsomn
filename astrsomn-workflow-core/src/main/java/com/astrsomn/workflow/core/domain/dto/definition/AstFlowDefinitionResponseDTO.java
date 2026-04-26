package com.astrsomn.workflow.core.domain.dto.definition;

import com.astrsomn.commn.base.BaseEntity;
import com.astrsomn.workflow.core.domain.entity.AstFlowDefinitionEntity;
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
