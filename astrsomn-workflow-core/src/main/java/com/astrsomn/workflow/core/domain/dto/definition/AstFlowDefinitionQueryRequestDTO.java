package com.astrsomn.workflow.core.domain.dto.definition;

import com.astrsomn.commn.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowDefinitionQueryRequestDTO extends BaseEntity<Long> {

    private String workflowKey;
    private String workflowName;
    private String description;
}
