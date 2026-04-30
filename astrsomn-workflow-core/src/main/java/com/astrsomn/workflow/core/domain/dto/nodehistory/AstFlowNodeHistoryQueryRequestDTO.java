package com.astrsomn.workflow.core.domain.dto.nodehistory;

import com.astrsomn.commn.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowNodeHistoryQueryRequestDTO extends BaseEntity<Long> {

    private Long instanceId;
    private Long flowDefinitionId;
    private Integer version;
    private String historyType;
    private String nodeId;
}
