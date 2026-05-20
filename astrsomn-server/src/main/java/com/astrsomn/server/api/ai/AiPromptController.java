package com.astrsomn.server.api.ai;

import com.astrsomn.api.runtime.common.dto.prompt.AiPromptCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptResponseDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptUpdateRequestDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.ai.AiPromptService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/history")
    public BaseResponse<List<AiPromptResponseDTO>> history(@RequestParam("promptKey") String promptKey, @RequestParam(value = "envCode", required = false) String envCode) {
        return aiPromptService.history(promptKey, envCode);
    }

    @GetMapping("/scene-tags")
    public BaseResponse<List<String>> querySceneTags() {
        return aiPromptService.querySceneTags();
    }


    @PostMapping("/submit")
    public BaseResponse<AiPromptResponseDTO> submit(@RequestBody AiPromptUpdateRequestDTO request) {
        return aiPromptService.submit(request);
    }

    @PostMapping("/beautify")
    public BaseResponse<String> beautify(@RequestBody AiPromptUpdateRequestDTO request) {
        return aiPromptService.beautify(request.getPromptContent());
    }


}
