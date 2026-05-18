package com.astrsomn.starter.runtime.config;

import com.astrsomn.starter.runtime.config.datasource.AstrsomnDatabaseAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;


@AutoConfiguration
@AutoConfigureAfter(AstrsomnDatabaseAutoConfiguration.class)
@ConditionalOnClass(name = "dev.langchain4j.model.chat.ChatModel")
@ComponentScan(basePackages = "com.astrsomn.starter.runtime.langchain")
public class AstrsomnAiAutoConfiguration {
}
