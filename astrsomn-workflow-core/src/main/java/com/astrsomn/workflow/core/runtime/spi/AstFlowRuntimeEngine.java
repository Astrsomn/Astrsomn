package com.astrsomn.workflow.core.runtime.spi;

import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunResponseDTO;

public interface AstFlowRuntimeEngine {

    AstFlowTestRunResponseDTO testRun(AstFlowTestRunRequestDTO request);
}
