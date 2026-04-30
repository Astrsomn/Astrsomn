package com.astrsomn.server.api.workflow;

import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.server.service.AiWorkflowInstanceService;
import com.astrsomn.workflow.core.domain.dto.instance.AstFlowInstanceCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.instance.AstFlowInstanceQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.instance.AstFlowInstanceResponseDTO;
import com.astrsomn.workflow.core.domain.dto.instance.AstFlowInstanceUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Workflow 实例控制器（骨架）。
 */
@RestController
@RequestMapping("/v1/astro/ai-workflow/instance")
@RequiredArgsConstructor
public class AiWorkflowInstanceController extends BaseController {

    private final AiWorkflowInstanceService aiWorkflowInstanceService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AstFlowInstanceCreateRequestDTO request) {
        return aiWorkflowInstanceService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiWorkflowInstanceService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AstFlowInstanceUpdateRequestDTO request) {
        return aiWorkflowInstanceService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AstFlowInstanceResponseDTO> queryPage(
            @RequestBody BasePageRequest<AstFlowInstanceQueryRequestDTO> request) {
        return aiWorkflowInstanceService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AstFlowInstanceResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiWorkflowInstanceService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
