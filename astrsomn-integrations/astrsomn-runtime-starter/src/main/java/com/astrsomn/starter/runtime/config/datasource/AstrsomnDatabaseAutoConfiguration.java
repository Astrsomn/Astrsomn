package com.astrsomn.starter.runtime.config.datasource;

import com.astrsomn.starter.runtime.config.AstrsomnPropertiesAutoConfiguration;
import com.astrsomn.starter.runtime.config.AstrsomnStarterRuntimeCondition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;

/**
 * 数据库基础设施自动配置。
 * <p>
 * 负责 Astrsomn 专用数据源、MyBatis-Plus、租户隔离、Schema 校验等数据库层 Bean 的注册。
 * 在 {@link AstrsomnPropertiesAutoConfiguration} 之后加载。
 */
@AutoConfiguration
@AutoConfigureAfter(AstrsomnPropertiesAutoConfiguration.class)
@Conditional(AstrsomnStarterRuntimeCondition.class)
@Import({
        AstrsomnDataSourceConfiguration.class,
        MybatisPlusConfig.class,
        DatabaseStartupValidationConfiguration.class
})
public class AstrsomnDatabaseAutoConfiguration {
}
