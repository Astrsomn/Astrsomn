package com.astrsomn.starter.runtime.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@AutoConfigureAfter(AstrsomnPropertiesAutoConfiguration.class)
@ComponentScan(basePackages = "com.astrsomn.starter")
@Conditional(AstrsomnStarterRuntimeCondition.class)
@Import({
        DataSourceConfig.class,  // 数据源初始化
        MybatisPlusConfig.class  // MyBatis-Plus 配置（含分页、环境隔离拦截器）
})
public class AstrsomnAutoConfiguration {
    // 这里通常不需要写代码，仅作为组件入口
}