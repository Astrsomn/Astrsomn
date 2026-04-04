package org.astrsomn.server.marketplace;

import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.dto.extension.ExtensionMarketplaceItemDTO;
import org.astrsomn.core.common.extension.marketplace.ExtensionMarketplaceCatalogSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 插件市场 Mock 目录，便于联调安装/卸载。后续可替换为扫描 providers 或远程目录的实现。
 */
@Component
public class MockExtensionMarketplaceCatalogSource implements ExtensionMarketplaceCatalogSource {

    private static final List<ExtensionMarketplaceItemDTO> ALL = List.of(
            ExtensionMarketplaceItemDTO.builder()
                    .extensionKey("mock-deepseek-market")
                    .extensionName("DeepSeek 模型扩展 (Mock)")
                    .type(SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode())
                    .version("1.0.0")
                    .author("Astrsomn")
                    .description("Mock 市场项：安装后可「加载模型」从 SPI 拉取 DeepSeek 可用模型清单写入 AI_MODEL。")
                    .providerCode("deepseek")
                    .build(),
            ExtensionMarketplaceItemDTO.builder()
                    .extensionKey("mock-pgvector-store")
                    .extensionName("PostgreSQL pgvector (Mock)")
                    .type(SystemExtensionEnum.ExtensionTypeEnum.VECTOR_STORE.getCode())
                    .version("0.1.0")
                    .author("Astrsomn")
                    .description("Mock 向量库扩展，用于市场列表与类型 Tab 展示。")
                    .build(),
            ExtensionMarketplaceItemDTO.builder()
                    .extensionKey("mock-mcp-filesystem")
                    .extensionName("MCP Filesystem (Mock)")
                    .type(SystemExtensionEnum.ExtensionTypeEnum.MCP.getCode())
                    .version("0.1.0")
                    .author("Astrsomn")
                    .description("Mock MCP 扩展，用于市场列表与类型 Tab 展示。")
                    .build()
    );

    @Override
    public List<ExtensionMarketplaceItemDTO> listCatalog(Optional<String> typeFilter) {
        String t = typeFilter.map(s -> s.trim().toUpperCase(Locale.ROOT)).filter(s -> !s.isEmpty()).orElse(null);
        if (t == null) {
            return List.copyOf(ALL);
        }
        return ALL.stream()
                .filter(item -> item.getType() != null && item.getType().equalsIgnoreCase(t))
                .toList();
    }
}
