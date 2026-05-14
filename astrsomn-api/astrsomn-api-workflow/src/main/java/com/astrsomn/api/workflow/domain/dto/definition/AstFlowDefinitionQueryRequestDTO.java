package com.astrsomn.api.workflow.domain.dto.definition;

import com.astrsomn.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowDefinitionQueryRequestDTO extends BaseEntity<Long> {

    private String workflowKey;
    private String workflowName;
    private String description;
}