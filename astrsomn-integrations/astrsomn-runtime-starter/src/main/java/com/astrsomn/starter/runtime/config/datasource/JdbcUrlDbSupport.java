package com.astrsomn.starter.runtime.config.datasource;

import com.baomidou.mybatisplus.annotation.DbType;

import java.util.Locale;


public final class JdbcUrlDbSupport {

    private JdbcUrlDbSupport() {
    }

    
    public static DbType resolveMybatisDbType(String jdbcUrl) {
        String raw = jdbcUrl != null ? jdbcUrl.trim() : "";
        if (raw.isEmpty()) {
            throw new IllegalArgumentException("spring.datasource.url must not be empty");
        }
        String lower = raw.toLowerCase(Locale.ROOT);
        if (lower.startsWith("jdbc:mysql:")) {
            return DbType.MYSQL;
        }
        throw new IllegalArgumentException(
                "Unsupported JDBC URL (only jdbc:mysql: is supported): " + truncateForMessage(raw));
    }

    public static boolean isSupportedJdbcUrl(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            return false;
        }
        try {
            resolveMybatisDbType(jdbcUrl);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static String truncateForMessage(String jdbcUrl) {
        int max = 120;
        if (jdbcUrl.length() <= max) {
            return jdbcUrl;
        }
        return jdbcUrl.substring(0, max) + "...";
    }
}
