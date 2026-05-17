package com.astrsomn.api.workflow.domain.dto.nodeconfig;

import com.astrsomn.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowNodeConfigQueryRequestDTO extends BaseEntity<Long> {

    private Long flowDefinitionId;
    private String nodeId;
    private String nodeType;
}