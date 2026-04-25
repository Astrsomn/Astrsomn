package com.astrsomn.workflow.core.domain.dto.definition;

import com.astrsomn.commn.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowDefinitionResponseDTO extends BaseEntity<Long> {

    private String workflowKey;
    private String workflowName;
    private Integer versionNo;
    private String description;
    private String graphJson;
    private String status;
}
