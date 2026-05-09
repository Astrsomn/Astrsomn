package com.astrsomn.starter.runtime.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 注册 {@link DatabaseStartupValidator}；仅在数据源与 SqlSessionFactory 已就绪时生效。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "astrsomn.data-base.validation", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean({DataSource.class, SqlSessionFactory.class})
public class DatabaseStartupValidationConfiguration {

    @Bean
    public DatabaseStartupValidator databaseStartupValidator(
            DataSource dataSource,
            AstrsomnProperties astrsomnProperties,
            SqlSessionFactory sqlSessionFactory) {
        return new DatabaseStartupValidator(dataSource, astrsomnProperties, sqlSessionFactory);
    }
}
