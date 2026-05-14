package com.astrsomn.api.workflow.runtime.spi;

import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunResponseDTO;

public interface AstFlowRuntimeEngine {

    AstFlowTestRunResponseDTO testRun(AstFlowTestRunRequestDTO request);
}