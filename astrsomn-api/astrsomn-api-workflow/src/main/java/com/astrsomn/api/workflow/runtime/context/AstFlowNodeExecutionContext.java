package com.astrsomn.api.workflow.runtime.context;

import com.astrsomn.api.workflow.runtime.model.AstFlowExecutableNode;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class AstFlowNodeExecutionContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private AstFlowExecutionContext executionContext;
    private AstFlowExecutableNode node;
    private int attemptNo;
    private Map<String, Object> nodeInput;
}