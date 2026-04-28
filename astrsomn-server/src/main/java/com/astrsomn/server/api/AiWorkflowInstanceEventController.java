package com.astrsomn.server.api;

import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.server.service.AiWorkflowInstanceEventService;
import com.astrsomn.workflow.core.domain.dto.instanceevent.AstFlowInstanceEventCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.instanceevent.AstFlowInstanceEventQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.instanceevent.AstFlowInstanceEventResponseDTO;
import com.astrsomn.workflow.core.domain.dto.instanceevent.AstFlowInstanceEventUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
