package com.astrsomn.starter.runtime.config;

import com.astrsomn.starter.runtime.config.datasource.AstrsomnDatabaseAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@AutoConfiguration
@AutoConfigureAfter(AstrsomnDatabaseAutoConfiguration.class)
@ConditionalOnClass(name = "dev.langchain4j.model.chat.ChatModel")
@ComponentScan(basePackages = "com.astrsomn.starter.runtime.langchain")
public class AstrsomnAiAutoConfiguration {

    /**
     * Astrsomn runtime internal task executor.
     * Used by AstroModelListener for async audit/quota tracking.
     * Isolated from application-level executor beans to avoid conflicts.
     */
    @Bean("astroTaskExecutor")
    public Executor astroTaskExecutor() {
        return Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "astrsomn-async-");
            t.setDaemon(true);
            return t;
        });
    }
}
