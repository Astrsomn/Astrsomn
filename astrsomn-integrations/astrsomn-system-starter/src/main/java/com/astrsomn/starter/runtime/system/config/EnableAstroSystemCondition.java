package com.astrsomn.starter.runtime.system.config;

import com.astrsomn.starter.runtime.config.annotation.EnableAstroRuntimeUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition that gates the system-management auto-configuration based on
 * {@code @EnableAstroRuntime(system = ...)} or the legacy
 * {@code astrsomn.enabled} property.
 */
public class EnableAstroSystemCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (EnableAstroRuntimeUtils.isPresent(context)) {
            return EnableAstroRuntimeUtils.getAttribute(context, "system", true);
        }
        // Backward compatible: when @EnableAstroRuntime is absent, system
        // follows the legacy astrsomn.enabled property.
        Boolean enabled = context.getEnvironment().getProperty(
                "astrsomn.enabled", Boolean.class, Boolean.TRUE);
        return enabled;
    }
}
