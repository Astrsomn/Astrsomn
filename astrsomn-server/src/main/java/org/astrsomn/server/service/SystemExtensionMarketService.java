package org.astrsomn.server.service;

import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.dto.extension.ExtensionMarketplaceItemDTO;
import org.astrsomn.core.common.base.PageResponse;

import java.util.Optional;

/**
 * 插件市场目录数据源。默认实现为 Mock，可替换为扫描 providers / 远程目录等。
 */
public interface SystemExtensionMarketService {

    /**
     * @param typeFilter 可选，与 {@link org.astrsomn.core.common.constant.SystemExtensionEnum.ExtensionTypeEnum} 的 code 一致
     */
    PageResponse<ExtensionMarketplaceItemDTO> listCatalog(int pageNo, int pageSize, Optional<String> typeFilter);

    /**
     * 从网络安装插件
     * @param pluginId
     * @param version
     * @return
     */
    BaseResponse<String> installExtension(String pluginId, String version);
}
