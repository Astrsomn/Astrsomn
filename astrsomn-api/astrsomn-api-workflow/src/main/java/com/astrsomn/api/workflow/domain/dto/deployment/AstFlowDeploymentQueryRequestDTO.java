package com.astrsomn.api.workflow.domain.dto.deployment;

import com.astrsomn.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowDeploymentQueryRequestDTO extends BaseEntity<Long> {

    private Long flowDefinitionId;
    private Integer version;
    private Boolean latest;
}