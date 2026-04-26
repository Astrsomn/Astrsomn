package com.astrsomn.workflow.core.runtime.context;

import com.astrsomn.workflow.core.runtime.model.AstFlowExecutableNode;
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
