package com.astrsomn.starter.runtime.vector.config;

import com.astrsomn.starter.runtime.config.annotation.EnableAstroRuntimeUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition that gates the vector-store auto-configuration based on
 * {@code @EnableAstroRuntime(vector = ...)} or the legacy
 * {@code astrsomn.enabled} property.
 */
public class EnableAstroVectorCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (EnableAstroRuntimeUtils.isPresent(context)) {
            return EnableAstroRuntimeUtils.getAttribute(context, "vector", true);
        }
        // Backward compatible: when @EnableAstroRuntime is absent, vector
        // follows the legacy astrsomn.enabled property.
        Boolean enabled = context.getEnvironment().getProperty(
                "astrsomn.enabled", Boolean.class, Boolean.TRUE);
        return enabled;
    }
}
