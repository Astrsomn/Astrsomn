package com.astrsomn.server.api;

import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.server.service.AiWorkflowPublishService;
import com.astrsomn.workflow.core.domain.dto.runtime.AstFlowPublishRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Workflow 发布控制器（骨架）。
 */
@RestController
@RequestMapping("/v1/astro/ai-workflow")
@RequiredArgsConstructor
public class AiWorkflowPublishController extends BaseController {

    private final AiWorkflowPublishService aiWorkflowPublishService;

    @PostMapping("/publish")
    public BaseResponse<String> publish(@RequestBody AstFlowPublishRequestDTO request) {
        return aiWorkflowPublishService.publish(request);
    }
}
