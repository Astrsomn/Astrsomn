package com.astrsomn.starter.runtime.schema;

import javax.sql.DataSource;


@FunctionalInterface
public interface SchemaInitializer {

    void initialize(DataSource dataSource);
}

