package com.astrsomn.starter.workflow.config;

import com.astrsomn.starter.runtime.config.annotation.EnableAstroRuntimeUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition that gates the workflow-engine auto-configuration based on
 * {@code @EnableAstroRuntime(workflow = ...)} or the legacy
 * {@code astrsomn.datasource.url} property.
 */
public class EnableAstroWorkflowCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (EnableAstroRuntimeUtils.isPresent(context)) {
            return EnableAstroRuntimeUtils.getAttribute(context, "workflow", true);
        }
        // Backward compatible: when @EnableAstroRuntime is absent, workflow
        // is gated by the existing @ConditionalOnProperty on the config class
        // — this condition passes through to let that check take effect.
        return true;
    }
}
