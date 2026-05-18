package com.astrsomn.server.api.workflow;

import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunRequestDTO;
import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowTestRunResponseDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.server.service.AiWorkflowTestRunService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
