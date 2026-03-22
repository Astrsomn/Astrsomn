package org.astrsomn.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "astrsomn")
public class AstrsomnProperties {


    private String envCode;


    private DataBase dataBase;

    @Data
    public static class DataBase{


        private String databaseType;


        private String host;


        private Integer port;


        private String databaseName;


        private String username;


        private String password;


        private String driver;


        private String url;


        private String schema;


        private Boolean useSsl = false;


        private String charset = "utf8";


        private String timezone = "Asia/Shanghai";


        private Integer connectionTimeout = 30000;


        private Integer maximumPoolSize = 10;


        private Integer minimumIdle = 5;
    }
}