package com.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.tool.AiToolCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.tool.AiToolQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.tool.AiToolResponseDTO;
import com.astrsomn.api.runtime.common.dto.tool.AiToolUpdateRequestDTO;
import com.astrsomn.server.service.AiToolService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-tool")
@RequiredArgsConstructor
public class AiToolController extends BaseController {

    private final AiToolService aiToolService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiToolCreateRequestDTO request) {
        return aiToolService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiToolService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiToolUpdateRequestDTO request) {
        return aiToolService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiToolResponseDTO> queryPage(@RequestBody BasePageRequest<AiToolQueryRequestDTO> request) {
        return aiToolService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiToolResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiToolService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
