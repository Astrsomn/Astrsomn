package org.astrsomn.starter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 使用 {@link Bean} + {@link ConfigurationProperties} 注册，避免
 * {@code @EnableConfigurationProperties(AstrsomnProperties.class)} 在部分环境下解析类名失败。
 */
@AutoConfiguration
@AutoConfigureBefore(AstrsomnAutoConfiguration.class)
public class AstrsomnPropertiesAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "astrsomn")
    public AstrsomnProperties astrsomnProperties() {
        return new AstrsomnProperties();
    }
}
