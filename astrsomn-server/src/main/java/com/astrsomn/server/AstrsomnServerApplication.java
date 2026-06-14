package com.astrsomn.server;

import com.astrsomn.starter.runtime.config.annotation.EnableAstroRuntime;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFileStorage
@EnableAstroRuntime
@SpringBootApplication(scanBasePackages = "com.astrsomn")
public class AstrsomnServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AstrsomnServerApplication.class, args);
    }
}
