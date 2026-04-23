package com.astrsomn.starter.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(AstrsomnProperties properties) {
        AstrsomnProperties.DataBase dataBase = properties.getDataBase();
        HikariDataSource dataSource = new HikariDataSource();

        // 1. 动态构建 URL
        String url = dataBase.getUrl();
        String dbTypeStr = dataBase.getDatabaseType() != null ? dataBase.getDatabaseType().toLowerCase() : "mysql";

        if (url == null || url.isEmpty()) {
                if ("mysql".equals(dbTypeStr)) {
                    url = String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=%s&serverTimezone=%s&useSSL=%s",
                            defaultIfNull(dataBase.getHost(), "localhost"),
                            defaultIfNull(dataBase.getPort(), 3306),
                            dataBase.getDatabaseName(),
                            defaultIfNull(dataBase.getCharset(), "utf8"),
                            defaultIfNull(dataBase.getTimezone(), "Asia/Shanghai"),
                            defaultIfNull(dataBase.getUseSsl(), false));
                    dataSource.setDriverClassName(defaultIfNull(dataBase.getDriver(), "com.mysql.cj.jdbc.Driver"));
                } else if ("oracle".equals(dbTypeStr)) {
                    url = dataBase.getSchema() != null
                            ? String.format("jdbc:oracle:thin:@%s:%d:%s?currentSchema=%s",
                            defaultIfNull(dataBase.getHost(), "localhost"), defaultIfNull(dataBase.getPort(), 1521), dataBase.getDatabaseName(), dataBase.getSchema())
                            : String.format("jdbc:oracle:thin:@%s:%d:%s",
                            defaultIfNull(dataBase.getHost(), "localhost"), defaultIfNull(dataBase.getPort(), 1521), dataBase.getDatabaseName());
                    dataSource.setDriverClassName(defaultIfNull(dataBase.getDriver(), "oracle.jdbc.OracleDriver"));
                } else if ("sqlite".equals(dbTypeStr)) {
                    String dbPath = dataBase.getDatabaseName() != null ? dataBase.getDatabaseName() : "astrsomn.db";
                    url = String.format("jdbc:sqlite:%s", dbPath);
                    dataSource.setDriverClassName(defaultIfNull(dataBase.getDriver(), "org.sqlite.JDBC"));
                }
            } else {
                // 如果用户直接写了 URL，也顺便设一下 Driver
                dataSource.setDriverClassName(dataBase.getDriver());
            }

        // 2. 公共基础配置
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(dataBase.getUsername());
        dataSource.setPassword(dataBase.getPassword());

        // 3. 连接池通用参数配置
        if (dataBase.getConnectionTimeout() != null) dataSource.setConnectionTimeout(dataBase.getConnectionTimeout());
        if (dataBase.getMaximumPoolSize() != null) dataSource.setMaximumPoolSize(dataBase.getMaximumPoolSize());
        if (dataBase.getMinimumIdle() != null) dataSource.setMinimumIdle(dataBase.getMinimumIdle());

        log.info(">>> [Astrsomn] {} DataSource initialized", dbTypeStr.toUpperCase());
        return dataSource;
    }

    private <T> T defaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static DbType getDbType(String databaseType) {
        if ("oracle".equalsIgnoreCase(databaseType)) return DbType.ORACLE;
        if ("sqlite".equalsIgnoreCase(databaseType)) return DbType.SQLITE;
        return DbType.MYSQL; // 默认 MySQL
    }
}