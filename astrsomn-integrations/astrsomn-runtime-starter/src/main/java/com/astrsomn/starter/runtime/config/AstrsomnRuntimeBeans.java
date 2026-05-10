package com.astrsomn.starter.runtime.config;

/**
 * Astrsomn runtime-starter 注册的 Spring Bean 名称（与宿主 {@code spring.datasource} 隔离，便于双数据源）。
 */
public final class AstrsomnRuntimeBeans {

    /** Astrsomn 专用 {@link javax.sql.DataSource}，非 {@code @Primary}。 */
    public static final String DATA_SOURCE = "astrsomnDataSource";

    /** Astrsomn {@link org.apache.ibatis.session.SqlSessionFactory}。 */
    public static final String SQL_SESSION_FACTORY = "astrsomnSqlSessionFactory";

    private AstrsomnRuntimeBeans() {
    }
}
