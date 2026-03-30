package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.config.SystemConfigCreateRequestDTO;
import org.astrsomn.core.common.dto.config.SystemConfigQueryRequestDTO;
import org.astrsomn.core.common.dto.config.SystemConfigResponseDTO;
import org.astrsomn.core.common.dto.config.SystemConfigUpdateRequestDTO;
import org.astrsomn.server.service.SystemConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/system-config")
@RequiredArgsConstructor
public class SystemConfigController extends BaseController {

    private final SystemConfigService systemConfigService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody SystemConfigCreateRequestDTO request) {
        return systemConfigService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return systemConfigService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody SystemConfigUpdateRequestDTO request) {
        return systemConfigService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<SystemConfigResponseDTO> queryPage(
            @RequestBody BasePageRequest<SystemConfigQueryRequestDTO> request) {
        return systemConfigService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<SystemConfigResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return systemConfigService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
