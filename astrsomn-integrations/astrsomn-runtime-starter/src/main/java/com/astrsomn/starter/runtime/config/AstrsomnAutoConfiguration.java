package com.astrsomn.starter.runtime.config;

import com.astrsomn.starter.runtime.config.datasource.AstrsomnDatabaseAutoConfiguration;
import com.astrsomn.starter.runtime.config.datasource.RuntimeMybatisContributor;
import com.astrsomn.starter.runtime.plugin.AstrsomnPluginManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Conditional;


@AutoConfiguration
@AutoConfigureAfter({AstrsomnPropertiesAutoConfiguration.class, DataSourceAutoConfiguration.class})
@Conditional(AstrsomnStarterRuntimeCondition.class)
@Import({AstrsomnDatabaseAutoConfiguration.class, AstrsomnAiAutoConfiguration.class,
         FreeMarkerConfiguration.class,
         AstrsomnAuditInterceptor.class, RuntimeMybatisContributor.class,
         AstrsomnPluginManager.class})
public class AstrsomnAutoConfiguration {
}
