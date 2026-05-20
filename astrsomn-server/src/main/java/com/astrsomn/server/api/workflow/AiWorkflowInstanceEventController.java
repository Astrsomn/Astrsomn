package com.astrsomn.server.api.workflow;

import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventResponseDTO;
import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventUpdateRequestDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.workflow.AiWorkflowInstanceEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-workflow/instance-event")
@RequiredArgsConstructor
public class AiWorkflowInstanceEventController extends BaseController {

    private final AiWorkflowInstanceEventService aiWorkflowInstanceEventService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AstFlowInstanceEventCreateRequestDTO request) {
        return aiWorkflowInstanceEventService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiWorkflowInstanceEventService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AstFlowInstanceEventUpdateRequestDTO request) {
        return aiWorkflowInstanceEventService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AstFlowInstanceEventResponseDTO> queryPage(
            @RequestBody BasePageRequest<AstFlowInstanceEventQueryRequestDTO> request) {
        return aiWorkflowInstanceEventService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AstFlowInstanceEventResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiWorkflowInstanceEventService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
