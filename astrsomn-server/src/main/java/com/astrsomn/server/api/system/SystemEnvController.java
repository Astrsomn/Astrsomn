package com.astrsomn.server.api.system;

import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.system.SystemEnvService;
import com.astrsomn.system.dto.env.SystemEnvCreateRequestDTO;
import com.astrsomn.system.dto.env.SystemEnvQueryRequestDTO;
import com.astrsomn.system.dto.env.SystemEnvResponseDTO;
import com.astrsomn.system.dto.env.SystemEnvUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
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
