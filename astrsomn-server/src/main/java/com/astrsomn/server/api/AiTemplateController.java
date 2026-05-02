package com.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateResponseDTO;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateUpdateRequestDTO;
import com.astrsomn.server.service.AiTemplateService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-template")
@RequiredArgsConstructor
public class AiTemplateController extends BaseController {

    private final AiTemplateService aiTemplateService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiTemplateCreateRequestDTO request) {
        return aiTemplateService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiTemplateService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiTemplateUpdateRequestDTO request) {
        return aiTemplateService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiTemplateResponseDTO> queryPage(@RequestBody BasePageRequest<AiTemplateQueryRequestDTO> request) {
        return aiTemplateService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiTemplateResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiTemplateService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
