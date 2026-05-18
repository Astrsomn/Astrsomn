package com.astrsomn.server.service.extension.base;

import com.astrsomn.system.constant.SystemExtensionEnum;
import com.astrsomn.system.dto.extension.ExtensionMarketplaceItemDTO;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;

import java.util.Optional;


public interface SystemExtensionMarketService {

    
    PageResponse<ExtensionMarketplaceItemDTO> listCatalog(int pageNo, int pageSize, Optional<String> typeFilter);

    
    BaseResponse<String> installExtension(String pluginId, String version);
}
