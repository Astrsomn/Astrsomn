package com.astrsomn.server.api.workflow;

import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxResponseDTO;
import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxUpdateRequestDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.workflow.AiWorkflowMsgOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-workflow/msg-outbox")
@RequiredArgsConstructor
public class AiWorkflowMsgOutboxController extends BaseController {

    private final AiWorkflowMsgOutboxService aiWorkflowMsgOutboxService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AstFlowMsgOutboxCreateRequestDTO request) {
        return aiWorkflowMsgOutboxService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiWorkflowMsgOutboxService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AstFlowMsgOutboxUpdateRequestDTO request) {
        return aiWorkflowMsgOutboxService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AstFlowMsgOutboxResponseDTO> queryPage(
            @RequestBody BasePageRequest<AstFlowMsgOutboxQueryRequestDTO> request) {
        return aiWorkflowMsgOutboxService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AstFlowMsgOutboxResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiWorkflowMsgOutboxService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
