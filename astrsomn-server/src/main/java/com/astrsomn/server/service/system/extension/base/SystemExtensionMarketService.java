package com.astrsomn.server.service.system.extension.base;

import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.system.dto.extension.ExtensionMarketplaceItemDTO;
import com.astrsomn.system.dto.extension.ExtensionMarketplaceVersionDTO;

import java.util.Optional;


public interface SystemExtensionMarketService {


    PageResponse<ExtensionMarketplaceItemDTO> listCatalog(int pageNo, int pageSize, Optional<String> typeFilter);


    BaseResponse<String> installExtension(String pluginId, String version);


    PageResponse<ExtensionMarketplaceVersionDTO> listVersions(String pluginId, int pageNo, int pageSize);
}
