package com.astrsomn.server.service.support;

import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.interceptor.EnvCodeRequestInterceptor;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.context.EnvRuntime;
import com.astrsomn.starter.runtime.context.EnvScope;
import org.springframework.stereotype.Component;

/**
 * 获取当前请求生效环境编码，与
 * {@link EnvCodeRequestInterceptor}、{@link EnvScope} 一致，
 * 便于超级管理员通过请求头切换工作空间后各列表数据隔离。
 */
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
