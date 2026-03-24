package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.mcp.AiMcpCreateRequestDTO;
import org.astrsomn.core.common.dto.mcp.AiMcpQueryRequestDTO;
import org.astrsomn.core.common.dto.mcp.AiMcpUpdateRequestDTO;
import org.astrsomn.core.common.dto.mcp.AiMcpResponseDTO;
import org.astrsomn.server.service.AiMcpService;
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
