package com.astrsomn.server.api.vector;

import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.vecsource.AiVecSourceCreateRequestDTO;
import com.astrsomn.core.common.dto.vecsource.AiVecSourceQueryRequestDTO;
import com.astrsomn.core.common.dto.vecsource.AiVecSourceResponseDTO;
import com.astrsomn.core.common.dto.vecsource.AiVecSourceSetStatusRequestDTO;
import com.astrsomn.core.common.dto.vecsource.AiVecSourceUpdateRequestDTO;
import com.astrsomn.server.service.AiVecSourceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-vec-source")
@RequiredArgsConstructor
public class AiVecSourceController extends BaseController {

    private final AiVecSourceService aiVecSourceService;

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

    /** 启用 / 禁用：更新 STATUS，并注册或移除运行时向量源连接缓存。 */
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
}
