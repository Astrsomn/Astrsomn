package org.astrsomn.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;

@SpringBootApplication(exclude = {MybatisPlusAutoConfiguration.class})
public class AstrsomnServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AstrsomnServerApplication.class, args);
    }
}