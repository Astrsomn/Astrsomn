package com.astrsomn.api.workflow.domain.dto.nodehistory;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowNodeHistoryCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
