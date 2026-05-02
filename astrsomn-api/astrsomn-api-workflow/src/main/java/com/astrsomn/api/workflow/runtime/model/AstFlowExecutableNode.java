package com.astrsomn.api.workflow.runtime.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class AstFlowExecutableNode implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nodeId;
    private String nodeType;
    private String nextNodeId;
    private Map<String, Object> config;
}
