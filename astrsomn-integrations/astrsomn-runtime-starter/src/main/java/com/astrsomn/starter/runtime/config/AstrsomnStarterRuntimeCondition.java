package com.astrsomn.starter.runtime.config;

import com.astrsomn.starter.runtime.config.datasource.JdbcUrlDbSupport;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.reflect.Method;


public class AstrsomnStarterRuntimeCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // Precedence: @EnableAstroRuntime annotation on any configuration class
        if (isEnableAstroRuntimePresent(context)) {
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

    /**
     * Check if any configuration class is annotated with @EnableAstroRuntime,
     * without hard-referencing EnableAstroRuntimeUtils at class loading time.
     * This allows the condition to work even when astrsomn-api-runtime is not on the classpath.
     */
    private boolean isEnableAstroRuntimePresent(ConditionContext context) {
        try {
            Class<?> utilsClass = Class.forName("com.astrsomn.starter.runtime.config.annotation.EnableAstroRuntimeUtils",
                    true, Thread.currentThread().getContextClassLoader());
            Method isPresent = utilsClass.getMethod("isPresent", ConditionContext.class);
            return (boolean) isPresent.invoke(null, context);
        } catch (ClassNotFoundException e) {
            // EnableAstroRuntimeUtils not on classpath -- skip annotation check
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
