package com.astrsomn.server.api.workflow;

import com.astrsomn.api.workflow.domain.dto.nodehistory.AstFlowNodeHistoryCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.nodehistory.AstFlowNodeHistoryQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.nodehistory.AstFlowNodeHistoryResponseDTO;
import com.astrsomn.api.workflow.domain.dto.nodehistory.AstFlowNodeHistoryUpdateRequestDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.workflow.AiWorkflowNodeHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-workflow/node-history")
@RequiredArgsConstructor
public class AiWorkflowNodeHistoryController extends BaseController {

    private final AiWorkflowNodeHistoryService aiWorkflowNodeHistoryService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AstFlowNodeHistoryCreateRequestDTO request) {
        return aiWorkflowNodeHistoryService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiWorkflowNodeHistoryService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AstFlowNodeHistoryUpdateRequestDTO request) {
        return aiWorkflowNodeHistoryService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AstFlowNodeHistoryResponseDTO> queryPage(
            @RequestBody BasePageRequest<AstFlowNodeHistoryQueryRequestDTO> request) {
        return aiWorkflowNodeHistoryService.queryPage(request);
    }

    @PostMapping("/queryPublishHistory")
    public PageResponse<AstFlowNodeHistoryResponseDTO> queryPublishHistory(
            @RequestBody BasePageRequest<AstFlowNodeHistoryQueryRequestDTO> request) {
        AstFlowNodeHistoryQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AstFlowNodeHistoryQueryRequestDTO();
            request.setParam(param);
        }
        if (param.getHistoryType() == null || param.getHistoryType().isBlank()) {
            param.setHistoryType("PUBLISH");
        }
        return aiWorkflowNodeHistoryService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AstFlowNodeHistoryResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiWorkflowNodeHistoryService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
