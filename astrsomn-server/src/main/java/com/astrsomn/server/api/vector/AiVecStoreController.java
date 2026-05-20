package com.astrsomn.server.api.vector;

import com.astrsomn.api.vector.dto.vecstore.*;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.vector.AiVecStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-vec-store")
@RequiredArgsConstructor
public class AiVecStoreController extends BaseController {

    private final AiVecStoreService aiVecStoreService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiVecStoreCreateRequestDTO request) {
        return aiVecStoreService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiVecStoreService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiVecStoreUpdateRequestDTO request) {
        return aiVecStoreService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiVecStoreResponseDTO> queryPage(@RequestBody BasePageRequest<AiVecStoreQueryRequestDTO> request) {
        return aiVecStoreService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiVecStoreResponseDTO> detail(@RequestParam("id") Long id) {
        return aiVecStoreService.detail(id);
    }

    @GetMapping("/stats")
    public BaseResponse<AiVecStoreStatsResponseDTO> stats(@RequestParam("id") Long id) {
        return aiVecStoreService.stats(id);
    }
}
