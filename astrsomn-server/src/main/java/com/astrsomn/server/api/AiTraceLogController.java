package com.astrsomn.server.api;

import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogResponseDTO;
import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogUpdateRequestDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.AiTraceLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-trace-log")
@RequiredArgsConstructor
public class AiTraceLogController extends BaseController {

    private final AiTraceLogService aiTraceLogService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiTraceLogCreateRequestDTO request) {
        return aiTraceLogService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiTraceLogService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiTraceLogUpdateRequestDTO request) {
        return aiTraceLogService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiTraceLogResponseDTO> queryPage(@RequestBody BasePageRequest<AiTraceLogQueryRequestDTO> request) {
        return aiTraceLogService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiTraceLogResponseDTO> detail(@RequestParam("id") Long id) {
        return aiTraceLogService.detail(id);
    }
}
