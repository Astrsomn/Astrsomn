package com.astrsomn.server.api.workflow;

import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.AiWorkflowTimerJobService;
import com.astrsomn.api.workflow.domain.dto.timerjob.AstFlowTimerJobCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.timerjob.AstFlowTimerJobQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.timerjob.AstFlowTimerJobResponseDTO;
import com.astrsomn.api.workflow.domain.dto.timerjob.AstFlowTimerJobUpdateRequestDTO;
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
@RequestMapping("/v1/astro/ai-workflow/timer-job")
@RequiredArgsConstructor
public class AiWorkflowTimerJobController extends BaseController {

    private final AiWorkflowTimerJobService aiWorkflowTimerJobService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AstFlowTimerJobCreateRequestDTO request) {
        return aiWorkflowTimerJobService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiWorkflowTimerJobService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AstFlowTimerJobUpdateRequestDTO request) {
        return aiWorkflowTimerJobService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AstFlowTimerJobResponseDTO> queryPage(
            @RequestBody BasePageRequest<AstFlowTimerJobQueryRequestDTO> request) {
        return aiWorkflowTimerJobService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AstFlowTimerJobResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiWorkflowTimerJobService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
