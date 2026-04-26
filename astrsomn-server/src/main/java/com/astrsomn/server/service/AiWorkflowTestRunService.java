package com.astrsomn.server.service;

import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunResponseDTO;

public interface AiWorkflowTestRunService {

    BaseResponse<AstFlowTestRunResponseDTO> testRun(AstFlowTestRunRequestDTO request);
}
