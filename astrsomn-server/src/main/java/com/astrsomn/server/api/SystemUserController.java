package com.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.user.SystemUserCreateRequestDTO;
import com.astrsomn.core.common.dto.user.SystemUserQueryRequestDTO;
import com.astrsomn.core.common.dto.user.SystemUserResponseDTO;
import com.astrsomn.core.common.dto.user.SystemUserUpdateRequestDTO;
import com.astrsomn.server.service.SystemUserService;
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
