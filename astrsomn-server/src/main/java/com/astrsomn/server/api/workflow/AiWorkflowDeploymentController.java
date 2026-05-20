package com.astrsomn.server.api.workflow;

import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentResponseDTO;
import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentUpdateRequestDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.workflow.AiWorkflowDeploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-workflow/deployment")
@RequiredArgsConstructor
public class AiWorkflowDeploymentController extends BaseController {

    private final AiWorkflowDeploymentService aiWorkflowDeploymentService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AstFlowDeploymentCreateRequestDTO request) {
        return aiWorkflowDeploymentService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiWorkflowDeploymentService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AstFlowDeploymentUpdateRequestDTO request) {
        return aiWorkflowDeploymentService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AstFlowDeploymentResponseDTO> queryPage(
            @RequestBody BasePageRequest<AstFlowDeploymentQueryRequestDTO> request) {
        return aiWorkflowDeploymentService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AstFlowDeploymentResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiWorkflowDeploymentService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
