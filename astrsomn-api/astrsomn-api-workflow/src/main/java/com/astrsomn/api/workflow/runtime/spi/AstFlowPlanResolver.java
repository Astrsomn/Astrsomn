package com.astrsomn.api.workflow.runtime.spi;

import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.api.workflow.runtime.model.AstFlowExecutablePlan;

public interface AstFlowPlanResolver {

    AstFlowExecutablePlan resolve(AstFlowTestRunRequestDTO request);
}