package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.env.SystemEnvCreateRequestDTO;
import org.astrsomn.core.common.dto.env.SystemEnvQueryRequestDTO;
import org.astrsomn.core.common.dto.env.SystemEnvUpdateRequestDTO;
import org.astrsomn.core.common.dto.env.SystemEnvResponseDTO;
import org.astrsomn.server.service.SystemEnvService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/system-env")
@RequiredArgsConstructor
public class SystemEnvController extends BaseController {

    private final SystemEnvService systemEnvService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody SystemEnvCreateRequestDTO request) {
        return systemEnvService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return systemEnvService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody SystemEnvUpdateRequestDTO request) {
        return systemEnvService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<SystemEnvResponseDTO> queryPage(@RequestBody BasePageRequest<SystemEnvQueryRequestDTO> request) {
        return systemEnvService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<SystemEnvResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return systemEnvService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
