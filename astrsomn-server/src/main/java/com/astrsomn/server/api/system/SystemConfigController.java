package com.astrsomn.server.api.system;

import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.config.SystemConfigCreateRequestDTO;
import com.astrsomn.core.common.dto.config.SystemConfigQueryRequestDTO;
import com.astrsomn.core.common.dto.config.SystemConfigResponseDTO;
import com.astrsomn.core.common.dto.config.SystemConfigUpdateRequestDTO;
import com.astrsomn.server.service.SystemConfigService;
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
