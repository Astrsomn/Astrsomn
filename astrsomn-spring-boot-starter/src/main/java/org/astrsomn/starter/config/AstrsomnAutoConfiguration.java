package org.astrsomn.starter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(AstrsomnProperties.class)
public class AstrsomnAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AstrsomnProperties astrsomnProperties() {
        return new AstrsomnProperties();
    }
}