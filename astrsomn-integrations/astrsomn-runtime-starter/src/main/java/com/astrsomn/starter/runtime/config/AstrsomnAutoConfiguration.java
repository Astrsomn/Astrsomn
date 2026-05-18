package com.astrsomn.starter.runtime.config;

import com.astrsomn.starter.runtime.config.datasource.AstrsomnDatabaseAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;


@AutoConfiguration
@AutoConfigureAfter(AstrsomnPropertiesAutoConfiguration.class)
@Conditional(AstrsomnStarterRuntimeCondition.class)
@Import({AstrsomnDatabaseAutoConfiguration.class, AstrsomnAiAutoConfiguration.class})
public class AstrsomnAutoConfiguration {
}
