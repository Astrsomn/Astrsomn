package com.astrsomn.starter.runtime.context;

import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


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

    
    public static boolean canSwitchEnv(AstrsomnProperties properties) {
        String username = UserContext.getUsername();
        if (StringUtils.isBlank(username)) {
            return false;
        }


        String defaultUser = properties != null ? properties.getUsername() : null;
        if (username.equalsIgnoreCase(defaultUser)) {
            return true;
        }


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
