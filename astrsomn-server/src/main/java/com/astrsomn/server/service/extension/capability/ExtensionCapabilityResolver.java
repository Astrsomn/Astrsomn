package com.astrsomn.server.service.extension.capability;

import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.entity.SystemExtensionEntity;
import com.astrsomn.core.common.utils.StringUtils;
import com.astrsomn.starter.langchain.factory.AstroModelFactory;
import com.astrsomn.starter.langchain.vector.AstroVecSourceFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExtensionCapabilityResolver {


    private final AstroModelFactory astroModelFactory;
    private final AstroVecSourceFactory astroVecSourceFactory;



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

