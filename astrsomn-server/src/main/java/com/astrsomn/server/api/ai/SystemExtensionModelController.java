package com.astrsomn.server.api.ai;

import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.server.service.ai.extension.SystemExtensionModelGuard;
import com.astrsomn.server.service.ai.extension.SystemExtensionModelSyncService;
import com.astrsomn.system.dto.extension.ExtensionModelLoadPreviewDTO;
import com.astrsomn.system.dto.extension.ExtensionModelUnloadPreviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/astro/system-extension")
@RequiredArgsConstructor
public class SystemExtensionModelController extends BaseController {

    private final SystemExtensionModelSyncService systemExtensionModelSyncService;
    private final SystemExtensionModelGuard systemExtensionModelGuard;

    @GetMapping("/load-models/preview")
    public BaseResponse<ExtensionModelLoadPreviewDTO> previewLoadModels(@RequestParam("id") Long id) {
        return systemExtensionModelSyncService.previewLoadModels(id);
    }

    @PostMapping("/load-models")
    public BaseResponse<String> loadModels(@RequestParam("id") Long id, @RequestParam(value = "modelKeys", required = false) String modelKeys) {
        return systemExtensionModelSyncService.loadModels(id, modelKeys);
    }

    @GetMapping("/unload-models/preview")
    public BaseResponse<ExtensionModelUnloadPreviewDTO> previewUnloadModels(@RequestParam("id") Long id) {
        return systemExtensionModelSyncService.previewUnloadModels(id);
    }

    @PostMapping("/unload-models")
    public BaseResponse<String> unloadModels(@RequestParam("id") Long id, @RequestParam(value = "modelKeys", required = false) String modelKeys) {
        return systemExtensionModelSyncService.unloadModels(id, modelKeys);
    }

    @PostMapping("/disable-provider-models")
    public BaseResponse<String> disableProviderModels(@RequestParam("id") Long id) {
        return systemExtensionModelGuard.disableAllModelsForExtension(id);
    }
}
