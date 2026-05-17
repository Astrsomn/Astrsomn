package com.astrsomn.server.config;

import com.astrsomn.starter.runtime.config.AstrsomnRuntimeBeans;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.astrsomn.server.mapper",
        sqlSessionFactoryRef = AstrsomnRuntimeBeans.SQL_SESSION_FACTORY)
public class MybatisConfiguration {
}
