package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.extension.*;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.server.service.SystemExtensionMarketService;
import org.astrsomn.server.service.SystemExtensionModelSyncService;
import org.astrsomn.server.service.SystemExtensionService;
import org.astrsomn.server.service.extension.guard.SystemExtensionModelGuard;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/v1/astro/system-extension")
@RequiredArgsConstructor
public class SystemExtensionController extends BaseController {

    private final SystemExtensionService systemExtensionService;
    private final SystemExtensionMarketService extensionMarketplaceCatalogSource;
    private final SystemExtensionModelSyncService systemExtensionModelSyncService;
    private final SystemExtensionModelGuard systemExtensionModelGuard;

    // ==================================== SystemExtensionService ====================================
    
    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody SystemExtensionCreateRequestDTO request) {
        return systemExtensionService.create(request);
    }

    @PostMapping(value = "/upload-jar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<String> uploadJar(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "extensionKey", required = false) String extensionKey,
            @RequestParam(value = "extensionName", required = false) String extensionName,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "version", required = false) String version,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "providerCode", required = false) String providerCode) {
        return systemExtensionService.uploadJar(
                file, extensionKey, extensionName, type, version, author, description, providerCode);
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

    // ==================================== ExtensionMarketplaceCatalogSource ====================================
    
    @GetMapping("/marketplace/catalog")
    public BaseResponse<PageResponse<ExtensionMarketplaceItemDTO>> marketplaceCatalog(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        String t = StringUtils.trimToNull(type);
        PageResponse<ExtensionMarketplaceItemDTO> pageResponse =
                extensionMarketplaceCatalogSource.listCatalog(pageNo, pageSize, Optional.ofNullable(t));
        return BaseResponse.success(pageResponse);
    }

    @PostMapping("/marketplace/install")
    public BaseResponse<String> installMarketplaceExtension(
            @RequestParam("pluginId") String pluginId,
            @RequestParam("version") String version) {
        return extensionMarketplaceCatalogSource.installExtension(pluginId, version);
    }

    // ==================================== SystemExtensionModelSyncService ====================================
    
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

    // ==================================== SystemExtensionModelGuard ====================================
    
    @PostMapping("/disable-provider-models")
    public BaseResponse<String> disableProviderModels(@RequestParam("id") Long id) {
        return systemExtensionModelGuard.disableAllModelsForExtension(id);
    }
}
