package com.astrsomn.server.api.workflow;

import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceResponseDTO;
import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceUpdateRequestDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.AiWorkflowInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


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
