package com.astrsomn.server.service.impl;

import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.server.service.AiWorkflowTestRunService;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class AiWorkflowTestRunServiceImpl implements AiWorkflowTestRunService {

    @Override
    public BaseResponse<AstFlowTestRunResponseDTO> testRun(AstFlowTestRunRequestDTO request) {
        // TODO M1: 同步测试运行（支持按草稿ID或 workflowKey）
        return BaseResponse.fail("TODO: test-run not implemented yet", null);
    }
}
