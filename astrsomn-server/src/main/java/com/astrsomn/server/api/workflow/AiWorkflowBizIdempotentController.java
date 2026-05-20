package com.astrsomn.server.api.workflow;

import com.astrsomn.api.workflow.domain.dto.bizidempotent.AstFlowBizIdempotentCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.bizidempotent.AstFlowBizIdempotentQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.bizidempotent.AstFlowBizIdempotentResponseDTO;
import com.astrsomn.api.workflow.domain.dto.bizidempotent.AstFlowBizIdempotentUpdateRequestDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.workflow.AiWorkflowBizIdempotentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-workflow/biz-idempotent")
@RequiredArgsConstructor
public class AiWorkflowBizIdempotentController extends BaseController {

    private final AiWorkflowBizIdempotentService aiWorkflowBizIdempotentService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AstFlowBizIdempotentCreateRequestDTO request) {
        return aiWorkflowBizIdempotentService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiWorkflowBizIdempotentService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AstFlowBizIdempotentUpdateRequestDTO request) {
        return aiWorkflowBizIdempotentService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AstFlowBizIdempotentResponseDTO> queryPage(
            @RequestBody BasePageRequest<AstFlowBizIdempotentQueryRequestDTO> request) {
        return aiWorkflowBizIdempotentService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AstFlowBizIdempotentResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiWorkflowBizIdempotentService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
