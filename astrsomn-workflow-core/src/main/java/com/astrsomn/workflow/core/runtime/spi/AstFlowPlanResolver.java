package com.astrsomn.workflow.core.runtime.spi;

import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.workflow.core.runtime.model.AstFlowExecutablePlan;

public interface AstFlowPlanResolver {

    AstFlowExecutablePlan resolve(AstFlowTestRunRequestDTO request);
}
