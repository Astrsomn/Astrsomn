package com.astrsomn.server.api.vector;

import com.astrsomn.api.vector.dto.vecsource.*;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.AiVecSourceService;
import com.astrsomn.starter.runtime.vector.AstroVecSourceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/astro/ai-vec-source")
@RequiredArgsConstructor
public class AiVecSourceController extends BaseController {

    private final AiVecSourceService aiVecSourceService;
    private final AstroVecSourceFactory astroVecSourceFactory;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiVecSourceCreateRequestDTO request) {
        return aiVecSourceService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiVecSourceService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/test-connection")
    public BaseResponse<String> testConnection(@RequestBody AiVecSourceCreateRequestDTO request) {
        return aiVecSourceService.testConnection(request);
    }

    /**
     * 启用 / 禁用：更新 STATUS，并注册或移除运行时向量源连接缓存。
     */
    @PostMapping("/set-status")
    public BaseResponse<String> setStatus(@RequestBody AiVecSourceSetStatusRequestDTO request) {
        return aiVecSourceService.setEnabledStatus(request);
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiVecSourceUpdateRequestDTO request) {
        return aiVecSourceService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiVecSourceResponseDTO> queryPage(@RequestBody BasePageRequest<AiVecSourceQueryRequestDTO> request) {
        return aiVecSourceService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiVecSourceResponseDTO> detail(@RequestParam("id") Long id) {
        return aiVecSourceService.detail(id);
    }

    @GetMapping("/available-drivers")
    public BaseResponse<List<AiVecDriverDTO>> availableDrivers() {
        return BaseResponse.success(astroVecSourceFactory.getAvailableDrivers());
    }
}
