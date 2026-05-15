package com.astrsomn.starter.runtime.config.datasource;

import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.config.AstrsomnRuntimeBeans;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 注册 {@link DatabaseStartupValidator}；仅在 Astrsomn 数据源与 SqlSessionFactory 已就绪时生效。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "astrsomn.data-base.validation", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(value = SqlSessionFactory.class, name = AstrsomnRuntimeBeans.SQL_SESSION_FACTORY)
public class DatabaseStartupValidationConfiguration {

    @Bean
    public DatabaseStartupValidator databaseStartupValidator(
            @Qualifier(AstrsomnRuntimeBeans.DATA_SOURCE) DataSource dataSource,
            AstrsomnDatasourceProperties datasourceProperties,
            AstrsomnProperties astrsomnProperties,
            @Qualifier(AstrsomnRuntimeBeans.SQL_SESSION_FACTORY) SqlSessionFactory sqlSessionFactory) {
        return new DatabaseStartupValidator(dataSource, datasourceProperties, astrsomnProperties, sqlSessionFactory);
    }
}
