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

        // 获取数据库类型
        String dbTypeStr = (properties.getDataBase() != null) ? properties.getDataBase().getDatabaseType() : "mysql";
        DbType dbType = DataSourceConfig.getDbType(dbTypeStr);

        // 1. 分页插件 (必须指定 DbType 以优化性能)
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));

        // 注意：这里不需要手动加 TenantLineInnerInterceptor
        // 因为我们在之前的 BeanPostProcessor 中已经实现了动态注入

        return interceptor;
    }

    /**
     * 注意：不要手动写 @Bean SqlSessionFactory。
     * 只需要在 starter 的 resources/META-INF/spring-configuration-metadata.json
     * 或者让用户在 application.yml 里配置：
     * mybatis-plus.mapper-locations=classpath:mapper/*.xml
     * mybatis-plus.type-aliases-package=org.astrsomn.core.common.entity
     */
}