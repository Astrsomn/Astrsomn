package com.astrsomn.starter.config;

import com.astrsomn.commn.utils.CryptoUtil;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.core.common.entity.SystemUserEntity;
import com.astrsomn.starter.context.UserContext;
import com.astrsomn.starter.mapper.SystemUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
 * 2. 校验配置的 username 是否存在于数据库中
 * 3. 初始化加密密钥配置
 */
@Slf4j
@AutoConfiguration
@AutoConfigureBefore(AstrsomnAutoConfiguration.class)
@ConditionalOnClass(SystemUserMapper.class)
public class AstrsomnPropertiesAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "astrsomn")
    public AstrsomnProperties astrsomnProperties() {
        return new AstrsomnProperties();
    }

    /**
     * 应用启动时初始化配置：
     * 1. 校验 username 配置
     * 2. 初始化加密密钥
     * 3. 设置默认用户到 UserContext
     */
    @Bean
    public Object astrsomnInitializer(AstrsomnProperties astrsomnProperties, SystemUserMapper systemUserMapper) {
        validateUsername(astrsomnProperties, systemUserMapper);
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
     * 校验配置的 username 是否存在于数据库中。
     */
    private void validateUsername(AstrsomnProperties astrsomnProperties, SystemUserMapper systemUserMapper) {
        String username = astrsomnProperties.getUsername();
        if (username == null || username.trim().isEmpty()) {
            String errorMsg = "Astrsomn configuration error: astrsomn.username must be configured";
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        
        username = username.trim();
        LambdaQueryWrapper<SystemUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemUserEntity::getUsername, username);
        
        SystemUserEntity user = systemUserMapper.selectOne(queryWrapper);
        
        if (user == null) {
            String errorMsg = String.format(
                "Astrsomn configuration error: username '%s' does not exist in SYS_USER table.",
                username
            );
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        
        log.info("Astrsomn configuration validation passed: username '{}' exists in database.", username);
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
