package com.astrsomn.starter.runtime.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link AstrsomnAutoConfiguration} 启用条件：{@code astrsomn.enabled=true}（默认）
 * 且已配置 {@code astrsomn.data-base.database-type}。
 */
public class AstrsomnStarterRuntimeCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Boolean enabled = context.getEnvironment().getProperty("astrsomn.enabled", Boolean.class, Boolean.TRUE);
        if (!Boolean.TRUE.equals(enabled)) {
            return false;
        }
        String dbType = context.getEnvironment().getProperty("astrsomn.data-base.database-type");
        return dbType != null && !dbType.isBlank();
    }
}
