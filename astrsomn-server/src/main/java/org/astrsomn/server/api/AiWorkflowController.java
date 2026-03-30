package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.workflow.*;
import org.astrsomn.server.service.AiWorkflowService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-workflow")
@RequiredArgsConstructor
public class AiWorkflowController extends BaseController {

    private final AiWorkflowService aiWorkflowService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiWorkflowCreateRequestDTO request) {
        return aiWorkflowService.create(request);
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiWorkflowUpdateRequestDTO request) {
        return aiWorkflowService.update(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiWorkflowService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @GetMapping("/detail")
    public BaseResponse<AiWorkflowResponseDTO> detail(@RequestParam("id") Long id) {
        return aiWorkflowService.detail(id);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiWorkflowResponseDTO> queryPage(@RequestBody BasePageRequest<AiWorkflowQueryRequestDTO> request) {
        return aiWorkflowService.queryPage(request);
    }

    @PostMapping("/publish")
    public BaseResponse<String> publish(@RequestParam("id") Long id) {
        return aiWorkflowService.publish(id);
    }

    @PostMapping("/test-run")
    public BaseResponse<AiWorkflowTestRunResponseDTO> testRun(@RequestBody AiWorkflowTestRunRequestDTO request) {
        return aiWorkflowService.testRun(request);
    }
}
