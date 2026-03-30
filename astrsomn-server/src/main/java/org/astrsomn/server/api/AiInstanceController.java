package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.instance.AiInstanceCreateRequestDTO;
import org.astrsomn.core.common.dto.instance.AiInstanceQueryRequestDTO;
import org.astrsomn.core.common.dto.instance.AiInstanceResponseDTO;
import org.astrsomn.core.common.dto.instance.AiInstanceUpdateRequestDTO;
import org.astrsomn.server.service.AiInstanceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-instance")
@RequiredArgsConstructor
public class AiInstanceController extends BaseController {

    private final AiInstanceService aiInstanceService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiInstanceCreateRequestDTO request) {
        return aiInstanceService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiInstanceService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiInstanceUpdateRequestDTO request) {
        return aiInstanceService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiInstanceResponseDTO> queryPage(
            @RequestBody BasePageRequest<AiInstanceQueryRequestDTO> request) {
        return aiInstanceService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiInstanceResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiInstanceService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
