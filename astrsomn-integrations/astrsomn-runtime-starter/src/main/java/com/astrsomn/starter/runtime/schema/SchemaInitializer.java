package com.astrsomn.starter.runtime.schema;

import javax.sql.DataSource;

/**
 * 数据库 Schema 初始化扩展点（SPI）。
 *
 * <p>runtime-starter 不直接绑定 Flyway/Liquibase 等迁移框架；由宿主应用（如 astrsomn-server）
 * 按需提供一个 {@link SchemaInitializer} Bean 来完成建表/迁移。</p>
 */
@FunctionalInterface
public interface SchemaInitializer {

    void initialize(DataSource dataSource);
}

