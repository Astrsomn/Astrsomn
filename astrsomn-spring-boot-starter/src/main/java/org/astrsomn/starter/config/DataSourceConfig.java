package org.astrsomn.starter.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "astrsomn.data-base", name = "database-type", havingValue = "mysql")
    public DataSource mysqlDataSource(AstrsomnProperties properties) {
        AstrsomnProperties.DataBase dataBase = properties.getDataBase();
        HikariDataSource dataSource = new HikariDataSource();
        
        String url = dataBase.getUrl();
        if (url == null || url.isEmpty()) {
            String host = dataBase.getHost() != null ? dataBase.getHost() : "localhost";
            Integer port = dataBase.getPort() != null ? dataBase.getPort() : 3306;
            String databaseName = dataBase.getDatabaseName();
            String charset = dataBase.getCharset() != null ? dataBase.getCharset() : "utf8";
            String timezone = dataBase.getTimezone() != null ? dataBase.getTimezone() : "Asia/Shanghai";
            Boolean useSsl = dataBase.getUseSsl() != null ? dataBase.getUseSsl() : false;
            
            url = String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=%s&serverTimezone=%s&useSSL=%s",
                    host, port, databaseName, charset, timezone, useSsl);
        }
        
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(dataBase.getUsername());
        dataSource.setPassword(dataBase.getPassword());
        
        if (dataBase.getDriver() != null && !dataBase.getDriver().isEmpty()) {
            dataSource.setDriverClassName(dataBase.getDriver());
        } else {
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        }
        
        if (dataBase.getConnectionTimeout() != null) {
            dataSource.setConnectionTimeout(dataBase.getConnectionTimeout());
        }
        if (dataBase.getMaximumPoolSize() != null) {
            dataSource.setMaximumPoolSize(dataBase.getMaximumPoolSize());
        }
        if (dataBase.getMinimumIdle() != null) {
            dataSource.setMinimumIdle(dataBase.getMinimumIdle());
        }
        
        log.info("MySQL DataSource initialized with URL: {}", url);
        return dataSource;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "astrsomn.data-base", name = "database-type", havingValue = "oracle")
    public DataSource oracleDataSource(AstrsomnProperties properties) {
        AstrsomnProperties.DataBase dataBase = properties.getDataBase();
        HikariDataSource dataSource = new HikariDataSource();
        
        String url = dataBase.getUrl();
        if (url == null || url.isEmpty()) {
            String host = dataBase.getHost() != null ? dataBase.getHost() : "localhost";
            Integer port = dataBase.getPort() != null ? dataBase.getPort() : 1521;
            String databaseName = dataBase.getDatabaseName();
            String schema = dataBase.getSchema();
            
            if (schema != null && !schema.isEmpty()) {
                url = String.format("jdbc:oracle:thin:@%s:%d:%s?currentSchema=%s", host, port, databaseName, schema);
            } else {
                url = String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, databaseName);
            }
        }
        
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(dataBase.getUsername());
        dataSource.setPassword(dataBase.getPassword());
        
        if (dataBase.getDriver() != null && !dataBase.getDriver().isEmpty()) {
            dataSource.setDriverClassName(dataBase.getDriver());
        } else {
            dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        }
        
        if (dataBase.getConnectionTimeout() != null) {
            dataSource.setConnectionTimeout(dataBase.getConnectionTimeout());
        }
        if (dataBase.getMaximumPoolSize() != null) {
            dataSource.setMaximumPoolSize(dataBase.getMaximumPoolSize());
        }
        if (dataBase.getMinimumIdle() != null) {
            dataSource.setMinimumIdle(dataBase.getMinimumIdle());
        }
        
        log.info("Oracle DataSource initialized with URL: {}", url);
        return dataSource;
    }

    public static DbType getDbType(String databaseType) {
        if (databaseType == null || databaseType.isEmpty()) {
            return DbType.MYSQL;
        }
        
        switch (databaseType.toLowerCase()) {
            case "mysql":
                return DbType.MYSQL;
            case "oracle":
                return DbType.ORACLE;
            default:
                log.warn("Unsupported database type: {}, defaulting to MYSQL", databaseType);
                return DbType.MYSQL;
        }
    }
}