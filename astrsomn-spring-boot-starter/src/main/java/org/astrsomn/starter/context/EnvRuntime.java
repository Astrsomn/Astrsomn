package org.astrsomn.starter.context;

import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.starter.config.AstrsomnProperties;

/**
 * 解析「当前生效」的环境编码：优先 {@link EnvScope}（请求头切换），否则回退实例配置。
 */
public final class EnvRuntime {

    private EnvRuntime() {
    }

    public static String resolveEffectiveEnvCode(AstrsomnProperties properties) {
        String fromScope = StringUtils.trimToNull(EnvScope.get());
        if (fromScope != null) {
            return fromScope;
        }
        String fromConfig = StringUtils.trimToNull(properties != null ? properties.getEnvCode() : null);
        return fromConfig != null ? fromConfig : "default";
    }
}
