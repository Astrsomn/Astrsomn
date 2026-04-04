package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.extension.ExtensionMarketplaceItemDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionCreateRequestDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionQueryRequestDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionResponseDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionUpdateRequestDTO;
import org.astrsomn.core.common.extension.marketplace.ExtensionMarketplaceCatalogSource;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.server.service.SystemExtensionModelSyncService;
import org.astrsomn.server.service.SystemExtensionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/astro/system-extension")
@RequiredArgsConstructor
public class SystemExtensionController extends BaseController {

    private final SystemExtensionService systemExtensionService;
    private final ExtensionMarketplaceCatalogSource extensionMarketplaceCatalogSource;
    private final SystemExtensionModelSyncService systemExtensionModelSyncService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody SystemExtensionCreateRequestDTO request) {
        return systemExtensionService.create(request);
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

    @PostMapping("/uninstall")
    public BaseResponse<String> uninstall(@RequestParam("id") Long id) {
        return systemExtensionService.uninstall(id);
    }

    @GetMapping("/marketplace/catalog")
    public BaseResponse<List<ExtensionMarketplaceItemDTO>> marketplaceCatalog(
            @RequestParam(value = "type", required = false) String type) {
        String t = StringUtils.trimToNull(type);
        List<ExtensionMarketplaceItemDTO> list =
                extensionMarketplaceCatalogSource.listCatalog(Optional.ofNullable(t));
        return BaseResponse.success(list);
    }

    @PostMapping("/load-models")
    public BaseResponse<String> loadModels(@RequestParam("id") Long id) {
        return systemExtensionModelSyncService.loadModels(id);
    }

    @PostMapping("/unload-models")
    public BaseResponse<String> unloadModels(@RequestParam("id") Long id) {
        return systemExtensionModelSyncService.unloadModels(id);
    }
}
