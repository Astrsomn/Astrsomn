package com.astrsomn.api.workflow.runtime.context;

import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.api.workflow.runtime.constant.AstFlowInstanceStateEnum;
import com.astrsomn.api.workflow.runtime.model.AstFlowExecutablePlan;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class AstFlowExecutionContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String traceId;
    private String envCode;
    private String instanceId;
    private String workflowKey;
    private AstFlowInstanceStateEnum instanceState;
    private AstFlowExecutablePlan executablePlan;
    @Builder.Default
    private Map<String, Object> variables = new HashMap<>();
    private AstFlowTestRunRequestDTO originalRequest;
}