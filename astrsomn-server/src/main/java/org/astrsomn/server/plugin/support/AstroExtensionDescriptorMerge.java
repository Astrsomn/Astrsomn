package org.astrsomn.server.plugin.support;

import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;
import org.astrsomn.core.common.util.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public final class AstroExtensionDescriptorMerge {

    private AstroExtensionDescriptorMerge() {}

    /**
     * Spring Bean 优先，SPI 仅补充尚未出现的 extensionKey（与 {@link org.astrsomn.server.plugin.SystemExtensionRegistry} 一致）。
     */
    public static Map<String, AstroExtensionDescriptor> merge(ApplicationContext applicationContext) {
        Map<String, AstroExtensionDescriptor> mergedByKey = new HashMap<>();
        applicationContext.getBeansOfType(AstroExtensionDescriptor.class).forEach((beanName, descriptor) -> {
            String extensionKey = StringUtils.trimToNull(descriptor.getExtensionKey());
            if (extensionKey != null) {
                mergedByKey.putIfAbsent(extensionKey, descriptor);
            }
        });
        ServiceLoader<AstroExtensionDescriptor> serviceLoader =
                ServiceLoader.load(AstroExtensionDescriptor.class);
        for (AstroExtensionDescriptor descriptor : serviceLoader) {
            if (descriptor == null) {
                continue;
            }
            String extensionKey = StringUtils.trimToNull(descriptor.getExtensionKey());
            if (extensionKey == null) {
                continue;
            }
            mergedByKey.putIfAbsent(extensionKey, descriptor);
        }
        return mergedByKey;
    }
}
