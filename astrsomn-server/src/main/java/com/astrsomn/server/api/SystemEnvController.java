package com.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.env.SystemEnvCreateRequestDTO;
import com.astrsomn.core.common.dto.env.SystemEnvQueryRequestDTO;
import com.astrsomn.core.common.dto.env.SystemEnvResponseDTO;
import com.astrsomn.core.common.dto.env.SystemEnvUpdateRequestDTO;
import com.astrsomn.server.service.SystemEnvService;
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
