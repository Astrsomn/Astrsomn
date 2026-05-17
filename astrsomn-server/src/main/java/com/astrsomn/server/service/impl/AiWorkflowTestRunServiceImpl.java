package com.astrsomn.server.service.impl;

import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunResponseDTO;
import com.astrsomn.api.workflow.runtime.spi.AstFlowRuntimeEngine;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.server.service.AiWorkflowTestRunService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiWorkflowTestRunServiceImpl implements AiWorkflowTestRunService {

    private final AstFlowRuntimeEngine astFlowRuntimeEngine;

    @Override
    public BaseResponse<AstFlowTestRunResponseDTO> testRun(AstFlowTestRunRequestDTO request) {
        if (request == null) {
            return BaseResponse.fail("request is required", null);
        }
        if (request.getId() == null && (request.getWorkflowKey() == null || request.getWorkflowKey().isBlank())) {
            return BaseResponse.fail("id or workflowKey is required", null);
        }

        AstFlowTestRunResponseDTO response = astFlowRuntimeEngine.testRun(request);
        return BaseResponse.success(response);
    }
}
