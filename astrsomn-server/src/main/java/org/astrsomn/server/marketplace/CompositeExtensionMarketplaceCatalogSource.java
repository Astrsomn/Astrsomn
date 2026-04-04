package org.astrsomn.server.marketplace;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.dto.extension.ExtensionMarketplaceItemDTO;
import org.astrsomn.core.common.extension.marketplace.ExtensionMarketplaceCatalogSource;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.server.plugin.support.AstroExtensionDescriptorMerge;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * 合并 SPI/Spring {@link AstroExtensionDescriptor} 与仅市场存在的 Mock 项，与 {@link org.astrsomn.server.plugin.SystemExtensionRegistry} 使用同一套 descriptor 合并规则。
 */
@Component
@RequiredArgsConstructor
public class CompositeExtensionMarketplaceCatalogSource implements ExtensionMarketplaceCatalogSource {

    private static final List<ExtensionMarketplaceItemDTO> MOCK_ONLY = List.of(
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

    private final ApplicationContext applicationContext;

    @Override
    public List<ExtensionMarketplaceItemDTO> listCatalog(Optional<String> typeFilter) {
        Map<String, ExtensionMarketplaceItemDTO> byKey = new LinkedHashMap<>();
        List<AstroExtensionDescriptor> descriptors = new ArrayList<>(
                AstroExtensionDescriptorMerge.merge(applicationContext).values());
        descriptors.sort(Comparator.comparing(AstroExtensionDescriptor::getName,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        for (AstroExtensionDescriptor d : descriptors) {
            String key = StringUtils.trimToNull(d.getExtensionKey());
            if (key == null) {
                continue;
            }
            byKey.put(key, toMarketplaceItem(d));
        }
        for (ExtensionMarketplaceItemDTO mock : MOCK_ONLY) {
            byKey.putIfAbsent(mock.getExtensionKey(), mock);
        }

        List<ExtensionMarketplaceItemDTO> list = new ArrayList<>(byKey.values());
        String t = typeFilter.map(s -> s.trim().toUpperCase(Locale.ROOT)).filter(s -> !s.isEmpty()).orElse(null);
        if (t == null) {
            return List.copyOf(list);
        }
        return list.stream()
                .filter(item -> item.getType() != null && item.getType().equalsIgnoreCase(t))
                .toList();
    }

    private static ExtensionMarketplaceItemDTO toMarketplaceItem(AstroExtensionDescriptor d) {
        return ExtensionMarketplaceItemDTO.builder()
                .extensionKey(d.getExtensionKey())
                .extensionName(d.getName())
                .type(d.getExtensionType() == null ? null : d.getExtensionType().getCode())
                .version(d.getVersion())
                .author(d.getAuthor())
                .description(d.getDescription())
                .avatar(d.getAvatar())
                .providerCode(d.getExtensionKey())
                .build();
    }
}
