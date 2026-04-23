package com.astrsomn.server.service.support;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 业务资源 key 统一生成：model / agent / prompt / tool / mcp 在各自表内、按
 * {@code CREATE_USER} 维度唯一；key 段之间使用冒号分隔，便于跨环境复用。
 */
@Component
public class BizResourceKeyGenerator {

    public static final int MAX_KEY_LEN = 120;
    private static final int MAX_SLUG_LEN = 48;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");


    @FunctionalInterface
    public interface KeyOccurrenceCounter {
        long count(String candidateKey);
    }
    /**
     * 生成基于日期的唯一业务键：业务代码+yyyyMMdd+001
     */
    public String generateDateBasedBizKey(BizKeyNamespace namespace, KeyOccurrenceCounter counter) {
        String shortCode = namespace.getShortCode();
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        
        for (int i = 1; i < 1000; i++) {
            String sequence = String.format("%03d", i);
            String candidate = shortCode + dateStr + sequence;
            if (counter.count(candidate) == 0) {
                return candidate;
            }
        }
        return shortCode + dateStr + "999";
    }
}
