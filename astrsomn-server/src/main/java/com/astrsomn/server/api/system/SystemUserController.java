package com.astrsomn.server.api.system;

import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.system.SystemUserService;
import com.astrsomn.system.dto.user.SystemUserCreateRequestDTO;
import com.astrsomn.system.dto.user.SystemUserQueryRequestDTO;
import com.astrsomn.system.dto.user.SystemUserResponseDTO;
import com.astrsomn.system.dto.user.SystemUserUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/system-user")
@RequiredArgsConstructor
public class SystemUserController extends BaseController {

    private final SystemUserService systemUserService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody SystemUserCreateRequestDTO request) {
        return systemUserService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return systemUserService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody SystemUserUpdateRequestDTO request) {
        return systemUserService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<SystemUserResponseDTO> queryPage(@RequestBody BasePageRequest<SystemUserQueryRequestDTO> request) {
        return systemUserService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<SystemUserResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return systemUserService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
