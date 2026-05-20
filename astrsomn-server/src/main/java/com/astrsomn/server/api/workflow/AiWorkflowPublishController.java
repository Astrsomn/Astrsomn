package com.astrsomn.server.api.workflow;

import com.astrsomn.api.workflow.domain.dto.runtime.AstFlowPublishRequestDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.server.service.workflow.AiWorkflowPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
