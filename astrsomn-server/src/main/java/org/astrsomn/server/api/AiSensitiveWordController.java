package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordCreateRequestDTO;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordQueryRequestDTO;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordResponseDTO;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordUpdateRequestDTO;
import org.astrsomn.server.service.AiSensitiveWordService;
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
