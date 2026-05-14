package com.astrsomn.server.api.system;

import com.astrsomn.api.runtime.common.dto.extension.ExtensionMarketplaceItemDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.service.extension.base.SystemExtensionMarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/astro/extension-marketplace")
@RequiredArgsConstructor
public class SystemExtensionMarketController extends BaseController {

    private final SystemExtensionMarketService extensionMarketplaceCatalogSource;

    @GetMapping("/catalog")
    public BaseResponse<PageResponse<ExtensionMarketplaceItemDTO>> catalog(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        String t = StringUtils.trimToNull(type);
        PageResponse<ExtensionMarketplaceItemDTO> pageResponse =
                extensionMarketplaceCatalogSource.listCatalog(pageNo, pageSize, Optional.ofNullable(t));
        return BaseResponse.success(pageResponse);
    }

    @PostMapping("/install")
    public BaseResponse<String> install(
            @RequestParam("pluginId") String pluginId,
            @RequestParam("version") String version) {
        return extensionMarketplaceCatalogSource.installExtension(pluginId, version);
    }
}
