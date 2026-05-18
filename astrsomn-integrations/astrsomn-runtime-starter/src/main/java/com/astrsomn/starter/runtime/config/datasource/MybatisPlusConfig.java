package com.astrsomn.starter.runtime.config.datasource;

import com.astrsomn.api.runtime.common.mybatis.AstrsomnMybatisContributor;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.config.AstrsomnRuntimeBeans;
import com.astrsomn.starter.runtime.schema.SchemaInitializer;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.*;

@Slf4j
@Configuration
public class MybatisPlusConfig {



    @Bean
    public Set<String> astrsomnTenantTables(List<AstrsomnMybatisContributor> contributors) {
        Set<String> tables = new LinkedHashSet<>();
        for (AstrsomnMybatisContributor c : contributors) {
            c.getTenantTables().stream()
                    .map(t -> t.toLowerCase().replace("`", ""))
                    .forEach(tables::add);
        }
        log.info("[Astrsomn Starter] 租户隔离表（共 {} 张）: {}", tables.size(), tables);
        return tables;
    }



    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(AstrsomnDatasourceProperties datasourceProperties,
                                                         Set<String> astrsomnTenantTables,
                                                         AstrsomnProperties properties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        String jdbcUrl = datasourceProperties != null ? datasourceProperties.getUrl() : null;
        DbType dbType = JdbcUrlDbSupport.resolveMybatisDbType(jdbcUrl);

        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(
                new EnvCodeTenantHandler(astrsomnTenantTables, properties)));
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));

        log.info("[Astrsomn Starter] 环境隔离插件已激活，作用表: {}", astrsomnTenantTables);

        return interceptor;
    }



    
    @Bean
    public MapperScannerConfigurer astrsomnMapperScannerConfigurer(List<AstrsomnMybatisContributor> contributors) {
        Set<String> packages = new LinkedHashSet<>();
        for (AstrsomnMybatisContributor c : contributors) {
            packages.addAll(c.getMapperScanPackages());
        }

        log.info("[Astrsomn Starter] Mapper 扫描包: {}", packages);

        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage(String.join(",", packages));
        configurer.setSqlSessionFactoryBeanName(AstrsomnRuntimeBeans.SQL_SESSION_FACTORY);
        return configurer;
    }



    @Bean(name = AstrsomnRuntimeBeans.SQL_SESSION_FACTORY)
    public SqlSessionFactory astrsomnSqlSessionFactory(
            @Qualifier(AstrsomnRuntimeBeans.DATA_SOURCE) DataSource dataSource,
            ObjectProvider<SchemaInitializer> schemaInitializer,
            AstrsomnProperties properties,
            MybatisPlusInterceptor mybatisPlusInterceptor,
            List<AstrsomnMybatisContributor> contributors,
            ObjectProvider<MetaObjectHandler> metaObjectHandlerProvider) throws Exception {
        schemaInitializer.ifAvailable(initializer -> initializer.initialize(dataSource));

        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);


        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath*:mapper/*.xml");

        if (properties.getMybatisPlus() != null
                && properties.getMybatisPlus().getAdditionalMapperLocations() != null
                && !properties.getMybatisPlus().getAdditionalMapperLocations().trim().isEmpty()) {
            mapperLocations.add(properties.getMybatisPlus().getAdditionalMapperLocations());
        }

        List<Resource> mapperResources = new ArrayList<>();
        for (String location : mapperLocations) {
            mapperResources.addAll(Arrays.asList(resolver.getResources(location)));
        }
        factoryBean.setMapperLocations(mapperResources.toArray(new Resource[0]));


        Set<String> typeAliasesPackages = new LinkedHashSet<>();

        typeAliasesPackages.add("com.astrsomn.core.common.entity");
        for (AstrsomnMybatisContributor c : contributors) {
            typeAliasesPackages.addAll(c.getTypeAliasesPackages());
        }
        if (properties.getMybatisPlus() != null
                && properties.getMybatisPlus().getAdditionalTypeAliasesPackage() != null
                && !properties.getMybatisPlus().getAdditionalTypeAliasesPackage().trim().isEmpty()) {
            typeAliasesPackages.add(properties.getMybatisPlus().getAdditionalTypeAliasesPackage());
        }
        factoryBean.setTypeAliasesPackage(String.join(",", typeAliasesPackages));


        factoryBean.setPlugins(mybatisPlusInterceptor);


        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        GlobalConfig globalConfig = GlobalConfigUtils.defaults()
                .setSqlInjector(new DefaultSqlInjector());
        globalConfig.getDbConfig().setCapitalMode(true);
        if (globalConfig.getIdentifierGenerator() == null) {
            globalConfig.setIdentifierGenerator(new DefaultIdentifierGenerator());
        }
        metaObjectHandlerProvider.ifAvailable(globalConfig::setMetaObjectHandler);
        GlobalConfigUtils.setGlobalConfig(mybatisConfiguration, globalConfig);
        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
        factoryBean.setConfiguration(mybatisConfiguration);

        log.info("[Astrsomn Starter] typeAliases 包: {}", typeAliasesPackages);

        return factoryBean.getObject();
    }
}
