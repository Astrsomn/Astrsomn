package com.astrsomn.starter.runtime.config;

import com.astrsomn.starter.runtime.config.annotation.EnableAstroRuntimeUtils;
import com.astrsomn.starter.runtime.config.datasource.JdbcUrlDbSupport;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


public class AstrsomnStarterRuntimeCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // Precedence: @EnableAstroRuntime annotation on any configuration class
        if (EnableAstroRuntimeUtils.isPresent(context)) {
            String jdbcUrl = context.getEnvironment().getProperty("astrsomn.datasource.url");
            return JdbcUrlDbSupport.isSupportedJdbcUrl(jdbcUrl);
        }
        // Fallback: YAML astrsomn.enabled property (backward compatibility)
        Boolean enabled = context.getEnvironment().getProperty("astrsomn.enabled", Boolean.class, Boolean.TRUE);
        if (!enabled) {
            return false;
        }
        String jdbcUrl = context.getEnvironment().getProperty("astrsomn.datasource.url");
        return JdbcUrlDbSupport.isSupportedJdbcUrl(jdbcUrl);
    }
}
