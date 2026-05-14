package com.astrsomn.api.workflow.runtime.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AstFlowExecutablePlan implements Serializable {

    private static final long serialVersionUID = 1L;

    private String planId;
    private String startNodeId;
    private Map<String, AstFlowExecutableNode> nodes;
    private List<AstFlowExecutableNode> orderedNodes;
}