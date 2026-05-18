package com.astrsomn.starter.runtime.config.datasource;

import lombok.Data;


@Data
public class AstrsomnDatasourceProperties {

    
    private String url;

    private String username;

    private String password;

    
    private String driverClassName;

    private HikariCp hikari = new HikariCp();

    @Data
    public static class HikariCp {

        
        private Long connectionTimeout;

        private Integer maximumPoolSize;

        private Integer minimumIdle;
    }
}
