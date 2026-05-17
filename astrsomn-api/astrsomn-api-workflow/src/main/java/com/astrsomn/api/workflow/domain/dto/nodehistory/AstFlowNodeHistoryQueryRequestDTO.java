package com.astrsomn.api.workflow.domain.dto.nodehistory;

import com.astrsomn.common.base.BaseEntity;
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