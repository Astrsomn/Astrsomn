package com.astrsomn.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AstrsomnServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AstrsomnServerApplication.class, args);
    }
}