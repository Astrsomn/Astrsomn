package com.astrsomn.workflow.core.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AstFlowNodeHistoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long instanceId;
    private String nodeId;
    private String inputData;
    private String outputData;
    private Long executionMs;
}
