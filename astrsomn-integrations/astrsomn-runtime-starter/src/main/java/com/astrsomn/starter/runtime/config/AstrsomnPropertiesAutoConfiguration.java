package com.astrsomn.starter.runtime.config;

import com.astrsomn.common.utils.CryptoUtil;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.config.datasource.AstrsomnDatasourceProperties;
import com.astrsomn.starter.runtime.context.UserContext;
import com.astrsomn.system.entity.SystemUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;


@Slf4j
@AutoConfiguration
@AutoConfigureBefore(AstrsomnAutoConfiguration.class)
@ConditionalOnClass(SystemUserEntity.class)
public class AstrsomnPropertiesAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "astrsomn")
    public AstrsomnProperties astrsomnProperties() {
        return new AstrsomnProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "astrsomn.datasource")
    public AstrsomnDatasourceProperties astrsomnDatasourceProperties() {
        return new AstrsomnDatasourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "astrsomn.resilience")
    public AstrsomnResilienceProperties astrsomnResilienceProperties() {
        return new AstrsomnResilienceProperties();
    }

    
    @Bean
    public Object astrsomnCryptoInitializer(AstrsomnProperties astrsomnProperties) {
        initCrypto(astrsomnProperties);
        initDefaultUser(astrsomnProperties);
        return new Object();
    }

    
    private void initDefaultUser(AstrsomnProperties astrsomnProperties) {
        String username = astrsomnProperties.getUsername();
        if (StringUtils.isNotBlank(username)) {
            UserContext.setUsername(username.trim());
            log.info("Astrsomn default user initialized: {}", username.trim());
        }
    }

    
    private void initCrypto(AstrsomnProperties astrsomnProperties) {
        try {
            String key = astrsomnProperties.getAccountKey();
            if (key == null || key.isEmpty()) {
                key = "astrsomn-account-key";
                log.warn("Account key not configured, using default key");
            }
            CryptoUtil.setKey(key);
            log.info("Crypto configuration initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize crypto configuration", e);
            throw new RuntimeException("Crypto initialization failed", e);
        }
    }
}
