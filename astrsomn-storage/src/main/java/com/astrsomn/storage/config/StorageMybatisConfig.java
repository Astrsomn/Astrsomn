package com.astrsomn.storage.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.astrsomn.storage.mapper")
public class StorageMybatisConfig {
}
