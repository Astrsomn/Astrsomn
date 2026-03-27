package org.astrsomn.starter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ComponentScan(basePackages = "org.astrsomn.starter")
@EnableConfigurationProperties(AstrsomnProperties.class)
// 只要配置了 astrsomn.data-base 相关的属性就开启
@ConditionalOnProperty(prefix = "astrsomn.data-base", name = "database-type")
@Import({
        DataSourceConfig.class,             // 负责数据源初始化（已合并 MySQL/Oracle）
        MybatisPlusConfig.class,           // 负责 MP 插件配置（已合并）
        AstrsomnEnvAutoConfiguration.class // 负责环境隔离拦截器的注入
})
public class AstrsomnAutoConfiguration {
    // 这里通常不需要写代码，仅作为组件入口
}