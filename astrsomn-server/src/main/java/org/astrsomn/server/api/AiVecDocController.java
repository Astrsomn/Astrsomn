package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocCreateRequestDTO;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocQueryRequestDTO;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocResponseDTO;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocUpdateRequestDTO;
import org.astrsomn.server.service.AiVecDocService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-vec-doc")
@RequiredArgsConstructor
public class AiVecDocController extends BaseController {

    private final AiVecDocService aiVecDocService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiVecDocCreateRequestDTO request) {
        return aiVecDocService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiVecDocService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiVecDocUpdateRequestDTO request) {
        return aiVecDocService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiVecDocResponseDTO> queryPage(@RequestBody BasePageRequest<AiVecDocQueryRequestDTO> request) {
        return aiVecDocService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiVecDocResponseDTO> detail(@RequestParam("id") Long id) {
        return aiVecDocService.detail(id);
    }
}
