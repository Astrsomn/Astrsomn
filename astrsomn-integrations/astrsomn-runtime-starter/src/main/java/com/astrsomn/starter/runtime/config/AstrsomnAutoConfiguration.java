package com.astrsomn.starter.runtime.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@AutoConfigureAfter(AstrsomnPropertiesAutoConfiguration.class)
@ComponentScan(basePackages = "com.astrsomn.starter")
// 只要配置了 astrsomn.data-base 相关的属性就开启（与 AstrsomnProperties 是否注册无关）
@ConditionalOnProperty(prefix = "astrsomn.data-base", name = "database-type")
@Import({
        DataSourceConfig.class,  // 数据源初始化
        MybatisPlusConfig.class  // MyBatis-Plus 配置（含分页、环境隔离拦截器）
})
public class AstrsomnAutoConfiguration {
    // 这里通常不需要写代码，仅作为组件入口
}