package com.astrsomn.starter.runtime.config.datasource;

import com.baomidou.mybatisplus.annotation.DbType;

import java.util.Locale;

/**
 * Maps {@code spring.datasource.url} to MyBatis-Plus {@link DbType}. Only MySQL and H2 are supported.
 */
public final class JdbcUrlDbSupport {

    private JdbcUrlDbSupport() {
    }

    /**
     * @throws IllegalArgumentException if URL is blank or not mysql/h2
     */
    public static DbType resolveMybatisDbType(String jdbcUrl) {
        String raw = jdbcUrl != null ? jdbcUrl.trim() : "";
        if (raw.isEmpty()) {
            throw new IllegalArgumentException("spring.datasource.url must not be empty");
        }
        String lower = raw.toLowerCase(Locale.ROOT);
        if (lower.startsWith("jdbc:mysql:")) {
            return DbType.MYSQL;
        }
        if (lower.startsWith("jdbc:h2:")) {
            return DbType.H2;
        }
        throw new IllegalArgumentException(
                "Unsupported JDBC URL (only jdbc:mysql: and jdbc:h2: are supported): " + truncateForMessage(raw));
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
