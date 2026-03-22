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


        private String baseUrl;


        private String username;


        private String driver;




        private String password;
    }





}