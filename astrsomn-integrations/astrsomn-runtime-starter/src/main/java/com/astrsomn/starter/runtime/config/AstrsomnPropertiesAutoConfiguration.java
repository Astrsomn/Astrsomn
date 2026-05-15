package com.astrsomn.starter.runtime.config;

import com.astrsomn.common.utils.CryptoUtil;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.context.UserContext;
import com.astrsomn.system.entity.SystemUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Astrsomn 配置自动配置类。
 * <p>
 * 使用 {@link Bean} + {@link ConfigurationProperties} 注册，避免
 * {@code @EnableConfigurationProperties(AstrsomnProperties.class)} 在部分环境下解析类名失败。
 * <p>
 * 职责：
 * 1. 注册 AstrsomnProperties 配置类
 * 2. 初始化加密密钥配置
 * 3. 设置默认用户到 UserContext
 */
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

    /**
     * 初始化加密密钥配置（不依赖数据库，可立即执行）
     */
    @Bean
    public Object astrsomnCryptoInitializer(AstrsomnProperties astrsomnProperties) {
        initCrypto(astrsomnProperties);
        initDefaultUser(astrsomnProperties);
        return new Object();
    }

    /**
     * 初始化默认用户到 UserContext。
     * <p>
     * 当没有登录用户时，使用配置的 username 作为默认创建者。
     */
    private void initDefaultUser(AstrsomnProperties astrsomnProperties) {
        String username = astrsomnProperties.getUsername();
        if (StringUtils.isNotBlank(username)) {
            UserContext.setUsername(username.trim());
            log.info("Astrsomn default user initialized: {}", username.trim());
        }
    }

    /**
     * 初始化加密密钥配置。
     */
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
