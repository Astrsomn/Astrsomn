package com.astrsomn.server.service.support;

import com.astrsomn.commn.base.BaseEntity;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.server.interceptor.EnvCodeRequestInterceptor;
import com.astrsomn.starter.context.EnvScope;
import com.astrsomn.starter.config.AstrsomnProperties;
import com.astrsomn.starter.context.EnvRuntime;
import org.springframework.stereotype.Component;

/**
 * 分页等业务查询统一按「当前请求生效环境」过滤，与
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

    /**
     * 将生效环境写入查询 DTO（覆盖请求体中的 envCode，避免客户端伪造环境）。
     */
    public void stampEffectiveEnv(BaseEntity<?> param) {
        if (param == null) {
            return;
        }
        String env = effectiveEnvCode();
        if (env != null) {
            param.setEnvCode(env);
        }
    }
}
