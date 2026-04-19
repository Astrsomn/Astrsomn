package org.astrsomn.starter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CryptoConfig implements InitializingBean {

    private final AstrsomnProperties astrsomnProperties;

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
            org.astrsomn.core.common.util.CryptoUtil.setKey(key);
            log.info("Crypto configuration initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize crypto configuration", e);
            throw new RuntimeException("Crypto initialization failed", e);
        }
    }
}
