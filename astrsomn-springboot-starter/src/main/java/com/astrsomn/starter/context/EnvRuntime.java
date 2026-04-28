package com.astrsomn.starter.context;

import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.starter.config.AstrsomnProperties;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 解析「当前生效」的环境编码：优先 {@link EnvScope}（请求头切换），否则回退实例配置。
 * <p>
 * 是否允许写入 {@link EnvScope} 由业务侧认证/鉴权拦截器控制。
 */
public final class EnvRuntime {

    private EnvRuntime() {
    }

    /**
     * 解析当前生效的环境编码。
     * <p>
     * 优先级：
     * 1. 优先使用 EnvScope（请求级切换）
     * 2. 否则使用配置文件中的 envCode
     * 3. 最后兜底为 "default"
     */
    public static String resolveEffectiveEnvCode(AstrsomnProperties properties) {
        // 请求级环境（由网关/拦截器按权限写入 EnvScope）优先级最高。
        String fromScope = StringUtils.trimToNull(EnvScope.get());
        if (fromScope != null) {
            return fromScope;
        }
        String fromConfig = StringUtils.trimToNull(properties != null ? properties.getEnvCode() : null);
        return fromConfig != null ? fromConfig : "default";
    }

    /**
     * 判断当前用户是否具有环境切换权限。
     * <p>
     * 管理员列表通过配置 astrsomn.admin-users 指定。
     */
    public static boolean canSwitchEnv(AstrsomnProperties properties) {
        String username = UserContext.getUsername();
        if (StringUtils.isBlank(username)) {
            return false;
        }
        
        // 配置的默认用户也可以切换环境
        String defaultUser = properties != null ? properties.getUsername() : null;
        if (username.equalsIgnoreCase(defaultUser)) {
            return true;
        }
        
        // 检查管理员用户列表
        String adminUsers = properties != null ? properties.getAdminUsers() : null;
        if (StringUtils.isNotBlank(adminUsers)) {
            Set<String> adminSet = new HashSet<>();
            Arrays.stream(adminUsers.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(adminSet::add);
            return adminSet.contains(username.trim());
        }
        
        return false;
    }
}
