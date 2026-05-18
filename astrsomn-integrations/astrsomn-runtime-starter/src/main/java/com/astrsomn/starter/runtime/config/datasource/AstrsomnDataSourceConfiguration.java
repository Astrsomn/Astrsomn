package com.astrsomn.starter.runtime.config.datasource;

import com.astrsomn.starter.runtime.config.AstrsomnRuntimeBeans;
import com.astrsomn.starter.runtime.langchain.exception.AstroConfigException;
import com.astrsomn.starter.runtime.langchain.exception.ErrorCode;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Slf4j
@Configuration
public class AstrsomnDataSourceConfiguration {

    @Bean(name = AstrsomnRuntimeBeans.DATA_SOURCE)
    public DataSource astrsomnDataSource(AstrsomnDatasourceProperties dsProps) {
        if (dsProps == null || dsProps.getUrl() == null || dsProps.getUrl().isBlank()) {
            throw new AstroConfigException(ErrorCode.DATASOURCE_URL_REQUIRED,
                    "astrsomn.datasource.url is required when astrsomn starter is enabled");
        }
        JdbcUrlDbSupport.resolveMybatisDbType(dsProps.getUrl());

        HikariDataSource hikari = new HikariDataSource();
        hikari.setPoolName("astrsomn-HikariPool");
        hikari.setJdbcUrl(dsProps.getUrl());
        hikari.setUsername(dsProps.getUsername());
        hikari.setPassword(dsProps.getPassword());
        if (dsProps.getDriverClassName() != null && !dsProps.getDriverClassName().isBlank()) {
            hikari.setDriverClassName(dsProps.getDriverClassName());
        }

        AstrsomnDatasourceProperties.HikariCp hikariCp = dsProps.getHikari();
        if (hikariCp != null) {
            if (hikariCp.getConnectionTimeout() != null) {
                hikari.setConnectionTimeout(hikariCp.getConnectionTimeout());
            }
            if (hikariCp.getMaximumPoolSize() != null) {
                hikari.setMaximumPoolSize(hikariCp.getMaximumPoolSize());
            }
            if (hikariCp.getMinimumIdle() != null) {
                hikari.setMinimumIdle(hikariCp.getMinimumIdle());
            }
        }

        log.info(">>> [Astrsomn] bean '{}' (Hikari) initialized — not @Primary; host may use spring.datasource as primary",
                AstrsomnRuntimeBeans.DATA_SOURCE);
        return hikari;
    }
}
