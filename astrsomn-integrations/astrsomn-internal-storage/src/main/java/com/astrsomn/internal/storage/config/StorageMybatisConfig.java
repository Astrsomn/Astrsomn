package com.astrsomn.internal.storage.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.astrsomn.*")
public class StorageMybatisConfig {
}
