package org.astrsomn.starter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties(AstrsomnProperties.class)
@ConditionalOnProperty(prefix = "astrsomn.data-base", name = "database-type")
@Import({DataSourceConfig.class, MybatisPlusConfig.class, MybatisPlusOracleConfig.class})
public class AstrsomnAutoConfiguration {
}