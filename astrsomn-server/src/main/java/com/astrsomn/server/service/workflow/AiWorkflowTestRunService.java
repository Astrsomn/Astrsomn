package com.astrsomn.server.service.workflow;

import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunResponseDTO;
import com.astrsomn.common.base.BaseResponse;

public interface AiWorkflowTestRunService {

    BaseResponse<AstFlowTestRunResponseDTO> testRun(AstFlowTestRunRequestDTO request);
}
