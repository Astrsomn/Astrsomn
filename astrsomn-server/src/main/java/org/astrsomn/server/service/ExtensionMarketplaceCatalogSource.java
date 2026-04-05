package org.astrsomn.server.service;

import org.astrsomn.core.common.dto.extension.ExtensionMarketplaceItemDTO;

import java.util.List;
import java.util.Optional;

/**
 * 插件市场目录数据源。默认实现为 Mock，可替换为扫描 providers / 远程目录等。
 */
public interface ExtensionMarketplaceCatalogSource {

    /**
     * @param typeFilter 可选，与 {@link org.astrsomn.core.common.constant.SystemExtensionEnum.ExtensionTypeEnum} 的 code 一致
     */
    List<ExtensionMarketplaceItemDTO> listCatalog(Optional<String> typeFilter);
}
