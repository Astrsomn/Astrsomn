package com.astrsomn.workflow.core.domain.dto.runtime;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class AstFlowTestRunResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String lastNodeId;
    private String message;
    private String traceId;
    private Map<String, Object> variables;
}
