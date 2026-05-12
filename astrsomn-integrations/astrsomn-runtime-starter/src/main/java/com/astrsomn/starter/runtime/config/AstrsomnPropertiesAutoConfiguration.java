package com.astrsomn.starter.runtime.config;

import com.astrsomn.common.utils.CryptoUtil;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.api.runtime.common.entity.SystemUserEntity;
import com.astrsomn.starter.runtime.context.UserContext;
import com.astrsomn.starter.runtime.mapper.AstSystemUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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
@ConditionalOnClass(AstSystemUserMapper.class)
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

/**
 * 延迟初始化配置，等待数据库表创建完成后执行
 */
@Slf4j
@Component
@ConditionalOnClass(AstSystemUserMapper.class)
class AstrsomnDbInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private AstrsomnProperties astrsomnProperties;
    private AstSystemUserMapper systemUserMapper;

    @Autowired(required = false)
    public void setAstrsomnProperties(AstrsomnProperties astrsomnProperties) {
        this.astrsomnProperties = astrsomnProperties;
    }

    @Autowired(required = false)
    public void setSystemUserMapper(AstSystemUserMapper systemUserMapper) {
        this.systemUserMapper = systemUserMapper;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (astrsomnProperties == null || systemUserMapper == null) {
            log.warn("AstrsomnDbInitializer: 必要依赖未注入，跳过数据库校验");
            return;
        }
        delayedValidate();
    }

    /**
     * 延迟校验，等待数据库表创建完成
     */
    private void delayedValidate() {
        try {
            validateUsername(astrsomnProperties, systemUserMapper);
        } catch (Exception e) {
            log.warn("AstrsomnDbInitializer: 数据库校验失败，将在延迟后重试 | 异常: {}", e.getMessage());
            try {
                TimeUnit.SECONDS.sleep(2);
                validateUsername(astrsomnProperties, systemUserMapper);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("AstrsomnDbInitializer: 数据库校验重试被中断");
            } catch (Exception e2) {
                log.error("AstrsomnDbInitializer: 数据库校验重试失败 | 异常: {}", e2.getMessage(), e2);
            }
        }
    }

    /**
     * 校验配置的 username 是否存在于数据库中。
     */
    private void validateUsername(AstrsomnProperties astrsomnProperties, AstSystemUserMapper systemUserMapper) {
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
}
