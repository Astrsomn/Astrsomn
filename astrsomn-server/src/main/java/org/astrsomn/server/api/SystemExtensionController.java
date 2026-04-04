package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.extension.ExtensionMarketplaceItemDTO;
import org.astrsomn.core.common.dto.extension.ExtensionModelLoadPreviewDTO;
import org.astrsomn.core.common.dto.extension.ExtensionModelUnloadPreviewDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionCreateRequestDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionQueryRequestDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionResponseDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionUpdateRequestDTO;
import org.astrsomn.core.common.extension.marketplace.ExtensionMarketplaceCatalogSource;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.server.service.SystemExtensionModelSyncService;
import org.astrsomn.server.service.SystemExtensionService;
import org.astrsomn.server.service.support.SystemExtensionModelGuard;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/astro/system-extension")
@RequiredArgsConstructor
public class SystemExtensionController extends BaseController {

    private final SystemExtensionService systemExtensionService;
    private final ExtensionMarketplaceCatalogSource extensionMarketplaceCatalogSource;
    private final SystemExtensionModelSyncService systemExtensionModelSyncService;
    private final SystemExtensionModelGuard systemExtensionModelGuard;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody SystemExtensionCreateRequestDTO request) {
        return systemExtensionService.create(request);
    }

    /**
     * 上传 jar 到进程工作目录下 {@code plugins}，并写入 {@code SYSTEM_EXTENSION}（已安装、未应用）。
     */
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

    /**
     * 模型类扩展：将当前环境下该厂商全部 AI 模型状态设为 disabled。
     */
    @PostMapping("/disable-provider-models")
    public BaseResponse<String> disableProviderModels(@RequestParam("id") Long id) {
        return systemExtensionModelGuard.disableAllModelsForExtension(id);
    }

    @GetMapping("/marketplace/catalog")
    public BaseResponse<List<ExtensionMarketplaceItemDTO>> marketplaceCatalog(
            @RequestParam(value = "type", required = false) String type) {
        String t = StringUtils.trimToNull(type);
        List<ExtensionMarketplaceItemDTO> list =
                extensionMarketplaceCatalogSource.listCatalog(Optional.ofNullable(t));
        return BaseResponse.success(list);
    }

    @GetMapping("/load-models/preview")
    public BaseResponse<ExtensionModelLoadPreviewDTO> previewLoadModels(@RequestParam("id") Long id) {
        return systemExtensionModelSyncService.previewLoadModels(id);
    }

    @PostMapping("/load-models")
    public BaseResponse<String> loadModels(@RequestParam("id") Long id) {
        return systemExtensionModelSyncService.loadModels(id);
    }

    @GetMapping("/unload-models/preview")
    public BaseResponse<ExtensionModelUnloadPreviewDTO> previewUnloadModels(@RequestParam("id") Long id) {
        return systemExtensionModelSyncService.previewUnloadModels(id);
    }

    @PostMapping("/unload-models")
    public BaseResponse<String> unloadModels(@RequestParam("id") Long id) {
        return systemExtensionModelSyncService.unloadModels(id);
    }
}
