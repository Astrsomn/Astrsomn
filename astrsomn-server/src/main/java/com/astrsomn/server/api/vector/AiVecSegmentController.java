package com.astrsomn.server.api.vector;

import com.astrsomn.api.vector.dto.vecsegment.*;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.AiVecSegmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/search")
    public BaseResponse<List<AiVecSegmentSearchResultDTO>> search(@RequestBody AiVecSegmentSearchRequestDTO request) {
        return BaseResponse.success(aiVecSegmentService.search(request));
    }
}
