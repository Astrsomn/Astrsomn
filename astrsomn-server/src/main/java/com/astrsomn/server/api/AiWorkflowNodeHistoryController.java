package com.astrsomn.server.api;

import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.server.service.AiWorkflowNodeHistoryService;
import com.astrsomn.workflow.core.domain.dto.nodehistory.AstFlowNodeHistoryCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.nodehistory.AstFlowNodeHistoryQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.nodehistory.AstFlowNodeHistoryResponseDTO;
import com.astrsomn.workflow.core.domain.dto.nodehistory.AstFlowNodeHistoryUpdateRequestDTO;
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

    @GetMapping("/detail")
    public BaseResponse<AstFlowNodeHistoryResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiWorkflowNodeHistoryService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
