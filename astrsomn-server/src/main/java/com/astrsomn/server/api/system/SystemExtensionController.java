package com.astrsomn.server.api.system;

import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.extension.base.SystemExtensionModelSyncService;
import com.astrsomn.server.service.extension.base.SystemExtensionService;
import com.astrsomn.server.service.extension.guard.SystemExtensionModelGuard;
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
    private final SystemExtensionModelSyncService systemExtensionModelSyncService;
    private final SystemExtensionModelGuard systemExtensionModelGuard;

    // ==================================== SystemExtensionService ====================================

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

    // ==================================== SystemExtensionModelSyncService ====================================

    @GetMapping("/load-models/preview")
    public BaseResponse<com.astrsomn.system.dto.extension.ExtensionModelLoadPreviewDTO> previewLoadModels(@RequestParam("id") Long id) {
        return systemExtensionModelSyncService.previewLoadModels(id);
    }

    @PostMapping("/load-models")
    public BaseResponse<String> loadModels(@RequestParam("id") Long id, @RequestParam(value = "modelKeys", required = false) String modelKeys) {
        return systemExtensionModelSyncService.loadModels(id, modelKeys);
    }

    @GetMapping("/unload-models/preview")
    public BaseResponse<com.astrsomn.system.dto.extension.ExtensionModelUnloadPreviewDTO> previewUnloadModels(@RequestParam("id") Long id) {
        return systemExtensionModelSyncService.previewUnloadModels(id);
    }

    @PostMapping("/unload-models")
    public BaseResponse<String> unloadModels(@RequestParam("id") Long id, @RequestParam(value = "modelKeys", required = false) String modelKeys) {
        return systemExtensionModelSyncService.unloadModels(id, modelKeys);
    }

    // ==================================== SystemExtensionModelGuard ====================================

    @PostMapping("/disable-provider-models")
    public BaseResponse<String> disableProviderModels(@RequestParam("id") Long id) {
        return systemExtensionModelGuard.disableAllModelsForExtension(id);
    }
}
