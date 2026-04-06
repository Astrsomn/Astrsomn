package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.prompt.AiPromptCreateRequestDTO;
import org.astrsomn.core.common.dto.prompt.AiPromptQueryRequestDTO;
import org.astrsomn.core.common.dto.prompt.AiPromptResponseDTO;
import org.astrsomn.core.common.dto.prompt.AiPromptUpdateRequestDTO;
import org.astrsomn.server.service.AiPromptService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/astro/ai-promopt")
@RequiredArgsConstructor
public class AiPromptController extends BaseController {

    private final AiPromptService aiPromptService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiPromptCreateRequestDTO request) {
        return aiPromptService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiPromptService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiPromptUpdateRequestDTO request) {
        return aiPromptService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiPromptResponseDTO> queryPage(@RequestBody BasePageRequest<AiPromptQueryRequestDTO> request) {
        return aiPromptService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiPromptResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiPromptService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    /**
     * 同一 promptKey 下全部历史版本（按版本倒序）。
     */
    @GetMapping("/history")
    public BaseResponse<List<AiPromptResponseDTO>> history(
            @RequestParam("promptKey") String promptKey,
            @RequestParam(value = "envCode", required = false) String envCode) {
        return aiPromptService.history(promptKey, envCode);
    }
}
