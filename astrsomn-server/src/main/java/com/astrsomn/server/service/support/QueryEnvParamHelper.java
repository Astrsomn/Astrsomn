package com.astrsomn.server.service.support;

import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.context.EnvRuntime;
import org.springframework.stereotype.Component;


@Component
public class QueryEnvParamHelper {

    private final AstrsomnProperties astrsomnProperties;

    public QueryEnvParamHelper(AstrsomnProperties astrsomnProperties) {
        this.astrsomnProperties = astrsomnProperties;
    }

    public String effectiveEnvCode() {
        return StringUtils.trimToNull(EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties));
    }
}
