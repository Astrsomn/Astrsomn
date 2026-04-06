package org.astrsomn.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.dto.extension.ExtensionMarketplaceItemDTO;
import org.astrsomn.server.service.ExtensionMarketplaceCatalogSource;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.server.plugin.SystemExtensionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 插件市场目录：仅来自 {@link SystemExtensionRegistry#mergeDescriptors(ApplicationContext)} 合并后的 Descriptor。
 */
@Component
@RequiredArgsConstructor
public class CompositeExtensionMarketplaceCatalogSource implements ExtensionMarketplaceCatalogSource {

    private final ApplicationContext applicationContext;

    @Override
    public List<ExtensionMarketplaceItemDTO> listCatalog(Optional<String> typeFilter) {
        List<AstroExtensionDescriptor> descriptors = new ArrayList<>(
                SystemExtensionRegistry.mergeDescriptors(applicationContext).values());
        descriptors.sort(Comparator.comparing(AstroExtensionDescriptor::getName,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));

        List<ExtensionMarketplaceItemDTO> list = new ArrayList<>();
        for (AstroExtensionDescriptor d : descriptors) {
            String key = StringUtils.trimToNull(d.getExtensionKey());
            if (key == null) {
                continue;
            }
            list.add(toMarketplaceItem(d));
        }

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
