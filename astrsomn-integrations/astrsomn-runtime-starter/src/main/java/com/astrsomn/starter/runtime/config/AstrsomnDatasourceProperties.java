package com.astrsomn.starter.runtime.config;

import lombok.Data;

/**
 * Astrsomn 专用数据源配置，前缀 {@code astrsomn.datasource}。
 * <p>与宿主 {@code spring.datasource} 分离，便于双数据源。</p>
 */
@Data
public class AstrsomnDatasourceProperties {

    /**
     * JDBC URL，仅支持 {@code jdbc:mysql:}、{@code jdbc:h2:}。
     */
    private String url;

    private String username;

    private String password;

    /**
     * 驱动类名；可选。
     */
    private String driverClassName;

    private HikariCp hikari = new HikariCp();

    @Data
    public static class HikariCp {

        /**
         * 连接超时（毫秒）。
         */
        private Long connectionTimeout;

        private Integer maximumPoolSize;

        private Integer minimumIdle;
    }
}
