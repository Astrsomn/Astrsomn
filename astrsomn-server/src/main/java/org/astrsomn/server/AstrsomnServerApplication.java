package org.astrsomn.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.astrsomn.core.mapper")
public class AstrsomnServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AstrsomnServerApplication.class, args);
    }
}