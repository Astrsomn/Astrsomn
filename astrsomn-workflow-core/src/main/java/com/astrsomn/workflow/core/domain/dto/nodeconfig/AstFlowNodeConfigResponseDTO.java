package com.astrsomn.workflow.core.domain.dto.nodeconfig;

import com.astrsomn.commn.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowNodeConfigResponseDTO extends BaseEntity<Long> {

    private Long flowDefinitionId;
    private String nodeId;
    private String nodeType;
    private String configJson;
}
