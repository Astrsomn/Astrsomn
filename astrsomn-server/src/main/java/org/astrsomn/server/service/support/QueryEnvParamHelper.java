package org.astrsomn.server.service.support;

import org.astrsomn.core.common.base.BaseEntity;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.context.EnvRuntime;
import org.springframework.stereotype.Component;

/**
 * 分页等业务查询统一按「当前请求生效环境」过滤，与
 * {@link org.astrsomn.server.interceptor.EnvCodeRequestInterceptor}、{@link org.astrsomn.starter.context.EnvScope} 一致，
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
