package com.astrsomn.server.service.extension.capability;

import com.astrsomn.api.runtime.common.entity.SystemExtensionEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.langchain.factory.AstroModelFactory;
import com.astrsomn.starter.runtime.langchain.vector.AstroVecSourceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExtensionCapabilityResolver {


    private final AstroModelFactory astroModelFactory;
    private final AstroVecSourceFactory astroVecSourceFactory;

    private static String resolveExtensionCode(SystemExtensionEntity extension) {
        if (extension == null) {
            return null;
        }
        String provider = StringUtils.trimToNull(extension.getExtensionCode());
        return provider != null ? provider : StringUtils.trimToNull(extension.getExtensionKey());
    }

    public boolean hasModelProviderCapability(SystemExtensionEntity extension) {
        String provider = resolveExtensionCode(extension);
        return provider != null && astroModelFactory.getHandler(provider).isPresent();
    }

    public boolean hasVectorDriverCapability(SystemExtensionEntity extension) {
        String provider = resolveExtensionCode(extension);
        return provider != null && astroVecSourceFactory.resolveDriver(provider).isPresent();
    }
}

