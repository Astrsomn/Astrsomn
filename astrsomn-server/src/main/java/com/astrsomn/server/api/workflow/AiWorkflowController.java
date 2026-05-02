package com.astrsomn.server.api.workflow;

import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.AiWorkflowService;
import com.astrsomn.api.workflow.domain.dto.definition.AstFlowDefinitionCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.definition.AstFlowDefinitionQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.definition.AstFlowDefinitionResponseDTO;
import com.astrsomn.api.workflow.domain.dto.definition.AstFlowDefinitionUpdateRequestDTO;
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
@RequestMapping("/v1/astro/ai-workflow")
@RequiredArgsConstructor
public class AiWorkflowController extends BaseController {

    private final AiWorkflowService aiWorkflowService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AstFlowDefinitionCreateRequestDTO request) {
        return aiWorkflowService.create(request);
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

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AstFlowDefinitionUpdateRequestDTO request) {
        return aiWorkflowService.update(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AstFlowDefinitionResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiWorkflowService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/queryPage")
    public PageResponse<AstFlowDefinitionResponseDTO> queryPage(
            @RequestBody BasePageRequest<AstFlowDefinitionQueryRequestDTO> request) {
        return aiWorkflowService.queryPage(request);
    }
}
