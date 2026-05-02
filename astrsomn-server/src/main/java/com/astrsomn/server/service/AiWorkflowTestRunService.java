package com.astrsomn.server.service;

import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunResponseDTO;

public interface AiWorkflowTestRunService {

    BaseResponse<AstFlowTestRunResponseDTO> testRun(AstFlowTestRunRequestDTO request);
}
