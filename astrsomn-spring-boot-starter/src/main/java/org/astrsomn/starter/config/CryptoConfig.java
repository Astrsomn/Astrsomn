package org.astrsomn.starter.config;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CryptoConfig implements InitializingBean {

    private final AstrsomnProperties astrsomnProperties;

    @Getter
    private AES aes;

    public CryptoConfig(AstrsomnProperties astrsomnProperties) {
        this.astrsomnProperties = astrsomnProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            String key = astrsomnProperties.getAccountKey();
            if (key == null || key.isEmpty()) {
                key = "astrsomn-account-key";
                log.warn("Account key not configured, using default key");
            }
            byte[] keyBytes = SecureUtil.md5(key).getBytes(StandardCharsets.UTF_8);
            this.aes = SecureUtil.aes(keyBytes);
            // 设置到 CryptoUtil
            org.astrsomn.core.common.util.CryptoUtil.setAes(this.aes);
            log.info("Crypto configuration initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize crypto configuration", e);
            throw new RuntimeException("Crypto initialization failed", e);
        }
    }
}
