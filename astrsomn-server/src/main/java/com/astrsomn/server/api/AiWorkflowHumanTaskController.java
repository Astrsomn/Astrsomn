package com.astrsomn.server.api;

import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.server.service.AiWorkflowHumanTaskService;
import com.astrsomn.workflow.core.domain.dto.humantask.AstFlowHumanTaskCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.humantask.AstFlowHumanTaskQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.humantask.AstFlowHumanTaskResponseDTO;
import com.astrsomn.workflow.core.domain.dto.humantask.AstFlowHumanTaskUpdateRequestDTO;
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
 * Workflow 人工任务控制器（骨架）。
 */
@RestController
@RequestMapping("/v1/astro/ai-workflow/human-task")
@RequiredArgsConstructor
public class AiWorkflowHumanTaskController extends BaseController {

    private final AiWorkflowHumanTaskService aiWorkflowHumanTaskService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AstFlowHumanTaskCreateRequestDTO request) {
        return aiWorkflowHumanTaskService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiWorkflowHumanTaskService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AstFlowHumanTaskUpdateRequestDTO request) {
        return aiWorkflowHumanTaskService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AstFlowHumanTaskResponseDTO> queryPage(
            @RequestBody BasePageRequest<AstFlowHumanTaskQueryRequestDTO> request) {
        return aiWorkflowHumanTaskService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AstFlowHumanTaskResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiWorkflowHumanTaskService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
