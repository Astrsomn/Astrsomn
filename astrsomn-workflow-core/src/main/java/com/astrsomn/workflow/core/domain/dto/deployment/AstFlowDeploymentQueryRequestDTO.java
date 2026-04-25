package com.astrsomn.workflow.core.domain.dto.deployment;

import com.astrsomn.commn.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowDeploymentQueryRequestDTO extends BaseEntity<Long> {

    private Long flowDefinitionId;
    private Integer version;
    private Boolean latest;
}
