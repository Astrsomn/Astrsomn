package com.astrsomn.starter.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@MapperScan("com.astrsomn.starter.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(AstrsomnProperties properties, TenantLineHandler tenantLineHandler) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        String dbTypeStr = (properties.getDataBase() != null) ? properties.getDataBase().getDatabaseType() : "mysql";
        DbType dbType = DataSourceConfig.getDbType(dbTypeStr);

        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(tenantLineHandler));
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));

        if (tenantLineHandler instanceof EnvCodeTenantHandler envHandler) {
            log.info("[Astrsomn Starter] 环境隔离插件已激活，作用表: {}", envHandler.getPrivateTables());
        }

        return interceptor;
    }

    /**
     * 环境隔离租户处理器
     */
    @Bean
    @ConditionalOnMissingBean(TenantLineHandler.class)
    public TenantLineHandler astrsomnEnvCodeTenantHandler() {
        return new EnvCodeTenantHandler();
    }

    @Bean
    public Interceptor reflectiveAuditAutoFillInterceptor(AstrsomnProperties properties) {
        return new ReflectiveAuditAutoFillInterceptor(properties);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(
            DataSource dataSource,
            AstrsomnProperties properties,
            MybatisPlusInterceptor mybatisPlusInterceptor,
            Interceptor reflectiveAuditAutoFillInterceptor,
            ObjectProvider<MetaObjectHandler> metaObjectHandlerProvider) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        
        String defaultMapperLocations = "classpath*:mapper/*.xml";
        String defaultTypeAliasesPackage = "com.astrsomn.core.common.entity";
        
        String mapperLocations = defaultMapperLocations;
        String typeAliasesPackage = defaultTypeAliasesPackage;
        
        if (properties.getMybatisPlus() != null) {
            if (properties.getMybatisPlus().getAdditionalMapperLocations() != null 
                    && !properties.getMybatisPlus().getAdditionalMapperLocations().trim().isEmpty()) {
                mapperLocations = defaultMapperLocations + "," + properties.getMybatisPlus().getAdditionalMapperLocations();
            }
            
            if (properties.getMybatisPlus().getAdditionalTypeAliasesPackage() != null 
                    && !properties.getMybatisPlus().getAdditionalTypeAliasesPackage().trim().isEmpty()) {
                typeAliasesPackage = defaultTypeAliasesPackage + "," + properties.getMybatisPlus().getAdditionalTypeAliasesPackage();
            }
        }
        
        List<Resource> mapperResources = new ArrayList<>();
        Arrays.stream(mapperLocations.split(","))
                .map(String::trim)
                .filter(location -> !location.isEmpty())
                .forEach(location -> {
                    try {
                        mapperResources.addAll(Arrays.asList(resolver.getResources(location)));
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to load mapper location: " + location, e);
                    }
                });
        factoryBean.setMapperLocations(mapperResources.toArray(new Resource[0]));
        factoryBean.setTypeAliasesPackage(typeAliasesPackage);
        factoryBean.setPlugins(mybatisPlusInterceptor, reflectiveAuditAutoFillInterceptor);
        
        // 关键：显式设置 MyBatis-Plus SqlInjector，确保 BaseMapper 默认方法
        // （如 selectList/selectById/insert 等）能够被注入到 mapped statements 中
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        GlobalConfig globalConfig = GlobalConfigUtils.defaults()
                .setSqlInjector(new DefaultSqlInjector());
        if (globalConfig.getIdentifierGenerator() == null) {
            globalConfig.setIdentifierGenerator(new DefaultIdentifierGenerator());
        }
        metaObjectHandlerProvider.ifAvailable(handler -> globalConfig.setMetaObjectHandler(handler));
        GlobalConfigUtils.setGlobalConfig(mybatisConfiguration, globalConfig);
        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
        factoryBean.setConfiguration(mybatisConfiguration);

        return factoryBean.getObject();
    }
}