package com.astrsomn.server.api.system;

import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.system.extension.base.SystemExtensionService;
import com.astrsomn.system.dto.extension.SystemExtensionCreateRequestDTO;
import com.astrsomn.system.dto.extension.SystemExtensionQueryRequestDTO;
import com.astrsomn.system.dto.extension.SystemExtensionResponseDTO;
import com.astrsomn.system.dto.extension.SystemExtensionUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/astro/system-extension")
@RequiredArgsConstructor
public class SystemExtensionController extends BaseController {

    private final SystemExtensionService systemExtensionService;



    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody SystemExtensionCreateRequestDTO request) {
        return systemExtensionService.create(request);
    }

    @PostMapping(value = "/upload-jar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<String> uploadJar(@RequestPart("file") MultipartFile file) {
        return systemExtensionService.uploadJar(file);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return systemExtensionService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody SystemExtensionUpdateRequestDTO request) {
        return systemExtensionService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<SystemExtensionResponseDTO> queryPage(
            @RequestBody BasePageRequest<SystemExtensionQueryRequestDTO> request) {
        return systemExtensionService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<SystemExtensionResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return systemExtensionService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/apply")
    public BaseResponse<String> apply(@RequestParam("id") Long id) {
        return systemExtensionService.apply(id);
    }

    @PostMapping("/revoke-apply")
    public BaseResponse<String> revokeApply(@RequestParam("id") Long id) {
        return systemExtensionService.revokeApply(id);
    }

    @PostMapping("/uninstall")
    public BaseResponse<String> uninstall(@RequestParam("id") Long id) {
        return systemExtensionService.uninstall(id);
    }
}
