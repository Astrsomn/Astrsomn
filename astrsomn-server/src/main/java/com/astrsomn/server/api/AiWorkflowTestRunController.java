package com.astrsomn.server.api;

import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.server.service.AiWorkflowTestRunService;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowTestRunResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Workflow 测试运行控制器（骨架）。
 */
@RestController
@RequestMapping("/v1/astro/ai-workflow")
@RequiredArgsConstructor
public class AiWorkflowTestRunController extends BaseController {

    private final AiWorkflowTestRunService aiWorkflowTestRunService;

    @PostMapping("/test-run")
    public BaseResponse<AstFlowTestRunResponseDTO> testRun(@RequestBody AstFlowTestRunRequestDTO request) {
        return aiWorkflowTestRunService.testRun(request);
    }
}
