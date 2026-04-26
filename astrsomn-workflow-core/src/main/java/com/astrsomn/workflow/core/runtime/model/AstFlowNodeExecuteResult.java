package com.astrsomn.workflow.core.runtime.model;

import com.astrsomn.workflow.core.runtime.constant.AstFlowNodeStateEnum;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class AstFlowNodeExecuteResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private AstFlowNodeStateEnum nodeState;
    private String nextNodeId;
    private String message;
    private String errorType;
    private Map<String, Object> outputVariables;
}
