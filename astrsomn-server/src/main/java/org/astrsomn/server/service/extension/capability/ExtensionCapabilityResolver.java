package org.astrsomn.server.service.extension.capability;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.server.plugin.registry.SystemExtensionRegistry;
import org.astrsomn.starter.langchain.factory.AstroModelFactory;
import org.astrsomn.starter.langchain.vector.AstroVecSourceFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExtensionCapabilityResolver {

    private final ApplicationContext applicationContext;
    private final AstroModelFactory astroModelFactory;
    private final AstroVecSourceFactory astroVecSourceFactory;

    public Map<String, AstroExtensionDescriptor> descriptorsByKey() {
        return SystemExtensionRegistry.mergeDescriptors(applicationContext);
    }

    public boolean hasModelProviderCapability(SystemExtensionEntity extension) {
        String provider = resolveProviderCode(extension);
        return provider != null && astroModelFactory.getHandler(provider).isPresent();
    }

    public boolean hasVectorDriverCapability(SystemExtensionEntity extension) {
        String provider = resolveProviderCode(extension);
        return provider != null && astroVecSourceFactory.resolveDriver(provider).isPresent();
    }

    private static String resolveProviderCode(SystemExtensionEntity extension) {
        if (extension == null) {
            return null;
        }
        String provider = StringUtils.trimToNull(extension.getProviderCode());
        return provider != null ? provider : StringUtils.trimToNull(extension.getExtensionKey());
    }
}

