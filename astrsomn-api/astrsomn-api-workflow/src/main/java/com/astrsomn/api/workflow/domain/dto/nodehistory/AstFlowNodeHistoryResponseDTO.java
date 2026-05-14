package com.astrsomn.api.workflow.domain.dto.nodehistory;

import com.astrsomn.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowNodeHistoryResponseDTO extends BaseEntity<Long> {

    private Long instanceId;
    private Long flowDefinitionId;
    private Integer version;
    private String historyType;
    private String nodeId;
    private String nodeName;
    private String inputData;
    private String outputData;
    private String snapshotJson;
    private Long executionMs;
}