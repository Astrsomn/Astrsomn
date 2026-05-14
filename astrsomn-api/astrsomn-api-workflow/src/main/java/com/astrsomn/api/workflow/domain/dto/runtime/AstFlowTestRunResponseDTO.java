package com.astrsomn.api.workflow.domain.dto.runtime;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class AstFlowTestRunResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String lastNodeId;
    private String message;
    private String traceId;
    private String errorType;
    private Map<String, Object> variables;
    private List<AstFlowNodeTraceDTO> nodeTraces;
}