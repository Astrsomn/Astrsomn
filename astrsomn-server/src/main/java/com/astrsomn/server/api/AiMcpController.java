package com.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.mcp.AiMcpCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.mcp.AiMcpQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.mcp.AiMcpResponseDTO;
import com.astrsomn.api.runtime.common.dto.mcp.AiMcpUpdateRequestDTO;
import com.astrsomn.server.service.AiMcpService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-mcp")
@RequiredArgsConstructor
public class AiMcpController extends BaseController {

    private final AiMcpService aiMcpService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiMcpCreateRequestDTO request) {
        return aiMcpService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiMcpService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiMcpUpdateRequestDTO request) {
        return aiMcpService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiMcpResponseDTO> queryPage(@RequestBody BasePageRequest<AiMcpQueryRequestDTO> request) {
        return aiMcpService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiMcpResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiMcpService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
