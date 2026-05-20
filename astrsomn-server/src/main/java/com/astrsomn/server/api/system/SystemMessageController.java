package com.astrsomn.server.api.system;

import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.system.SystemMessageService;
import com.astrsomn.system.dto.systemmessage.SystemMessageCreateRequestDTO;
import com.astrsomn.system.dto.systemmessage.SystemMessageQueryRequestDTO;
import com.astrsomn.system.dto.systemmessage.SystemMessageResponseDTO;
import com.astrsomn.system.dto.systemmessage.SystemMessageUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/system-message")
@RequiredArgsConstructor
public class SystemMessageController extends BaseController {

    private final SystemMessageService systemMessageService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody SystemMessageCreateRequestDTO request) {
        return systemMessageService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return systemMessageService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody SystemMessageUpdateRequestDTO request) {
        return systemMessageService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<SystemMessageResponseDTO> queryPage(
            @RequestBody BasePageRequest<SystemMessageQueryRequestDTO> request) {
        return systemMessageService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<SystemMessageResponseDTO> detail(@RequestParam("id") Long id) {
        return systemMessageService.detail(id);
    }
}
