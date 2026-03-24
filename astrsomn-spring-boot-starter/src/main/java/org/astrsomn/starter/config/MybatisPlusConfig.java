package org.astrsomn.starter.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan("org.astrsomn.core.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(AstrsomnProperties properties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        String dbTypeStr = (properties.getDataBase() != null) ? properties.getDataBase().getDatabaseType() : "mysql";
        DbType dbType = DataSourceConfig.getDbType(dbTypeStr);

        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));

        return interceptor;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, AstrsomnProperties properties) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        
        String defaultMapperLocations = "classpath:mapper/*.xml";
        String defaultTypeAliasesPackage = "org.astrsomn.core.common.entity";
        
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
        
        factoryBean.setMapperLocations(resolver.getResources(mapperLocations));
        factoryBean.setTypeAliasesPackage(typeAliasesPackage);
        
        return factoryBean.getObject();
    }
}