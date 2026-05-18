package com.astrsomn.starter.runtime.config.datasource;

import com.astrsomn.starter.runtime.config.AstrsomnPropertiesAutoConfiguration;
import com.astrsomn.starter.runtime.config.AstrsomnStarterRuntimeCondition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;


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
