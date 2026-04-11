package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecdriver.AiVecDriverCreateRequestDTO;
import org.astrsomn.core.common.dto.vecdriver.AiVecDriverQueryRequestDTO;
import org.astrsomn.core.common.dto.vecdriver.AiVecDriverResponseDTO;
import org.astrsomn.core.common.dto.vecdriver.AiVecDriverUpdateRequestDTO;
import org.astrsomn.server.service.AiVecDriverService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/astro/ai-vec-driver")
@RequiredArgsConstructor
public class AiVecDriverController extends BaseController {

    private final AiVecDriverService aiVecDriverService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiVecDriverCreateRequestDTO request) {
        return aiVecDriverService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiVecDriverService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiVecDriverUpdateRequestDTO request) {
        return aiVecDriverService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiVecDriverResponseDTO> queryPage(@RequestBody BasePageRequest<AiVecDriverQueryRequestDTO> request) {
        return aiVecDriverService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiVecDriverResponseDTO> detail(@RequestParam("id") Long id) {
        return aiVecDriverService.detail(id);
    }

    @GetMapping("/list")
    public BaseResponse<List<AiVecDriverResponseDTO>> listForSelect(@RequestParam(value = "status", required = false) String status) {
        return aiVecDriverService.listForSelect(status);
    }
}
