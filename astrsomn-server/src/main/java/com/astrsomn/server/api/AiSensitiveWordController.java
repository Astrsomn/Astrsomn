package com.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.sensitiveword.AiSensitiveWordCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.sensitiveword.AiSensitiveWordQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.sensitiveword.AiSensitiveWordResponseDTO;
import com.astrsomn.api.runtime.common.dto.sensitiveword.AiSensitiveWordUpdateRequestDTO;
import com.astrsomn.server.service.AiSensitiveWordService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/astro/ai-sensitive-word")
@RequiredArgsConstructor
public class AiSensitiveWordController extends BaseController {

    private final AiSensitiveWordService aiSensitiveWordService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiSensitiveWordCreateRequestDTO request) {
        return aiSensitiveWordService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiSensitiveWordService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiSensitiveWordUpdateRequestDTO request) {
        return aiSensitiveWordService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiSensitiveWordResponseDTO> queryPage(
            @RequestBody BasePageRequest<AiSensitiveWordQueryRequestDTO> request) {
        return aiSensitiveWordService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiSensitiveWordResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiSensitiveWordService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
