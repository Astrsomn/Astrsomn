package com.astrsomn.server.api.workflow;

import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.server.service.AiWorkflowNodeConfigService;
import com.astrsomn.workflow.core.domain.dto.nodeconfig.AstFlowNodeConfigCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.nodeconfig.AstFlowNodeConfigQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.nodeconfig.AstFlowNodeConfigResponseDTO;
import com.astrsomn.workflow.core.domain.dto.nodeconfig.AstFlowNodeConfigUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-workflow/node-config")
@RequiredArgsConstructor
public class AiWorkflowNodeConfigController extends BaseController {

    private final AiWorkflowNodeConfigService aiWorkflowNodeConfigService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AstFlowNodeConfigCreateRequestDTO request) {
        return aiWorkflowNodeConfigService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiWorkflowNodeConfigService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AstFlowNodeConfigUpdateRequestDTO request) {
        return aiWorkflowNodeConfigService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AstFlowNodeConfigResponseDTO> queryPage(
            @RequestBody BasePageRequest<AstFlowNodeConfigQueryRequestDTO> request) {
        return aiWorkflowNodeConfigService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AstFlowNodeConfigResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiWorkflowNodeConfigService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
