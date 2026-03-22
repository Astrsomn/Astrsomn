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
@ConditionalOnProperty(prefix = "astrsomn.data-base", name = "database-type", havingValue = "mysql")
@MapperScan("org.astrsomn.core.mapper")
public class MybatisPlusConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        sessionFactory.setTypeAliasesPackage("org.astrsomn.core.common.entity");
        return sessionFactory.getObject();
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(AstrsomnProperties properties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        DbType dbType = DbType.MYSQL;
        if (properties != null && properties.getDataBase() != null) {
            dbType = DataSourceConfig.getDbType(properties.getDataBase().getDatabaseType());
        }
        
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
        return interceptor;
    }
}