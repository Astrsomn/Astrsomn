package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentCreateRequestDTO;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentQueryRequestDTO;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentResponseDTO;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentUpdateRequestDTO;
import org.astrsomn.server.service.AiVecSegmentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-vec-segment")
@RequiredArgsConstructor
public class AiVecSegmentController extends BaseController {

    private final AiVecSegmentService aiVecSegmentService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiVecSegmentCreateRequestDTO request) {
        return aiVecSegmentService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiVecSegmentService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiVecSegmentUpdateRequestDTO request) {
        return aiVecSegmentService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiVecSegmentResponseDTO> queryPage(@RequestBody BasePageRequest<AiVecSegmentQueryRequestDTO> request) {
        return aiVecSegmentService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiVecSegmentResponseDTO> detail(@RequestParam("id") Long id) {
        return aiVecSegmentService.detail(id);
    }
}
