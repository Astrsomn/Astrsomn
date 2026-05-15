package com.astrsomn.starter.runtime.config;

import com.astrsomn.starter.runtime.config.datasource.AstrsomnDatabaseAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;

/**
 * AI 编排层自动配置。
 * <p>
 * 扫描 {@code com.astrsomn.starter.runtime.langchain} 包下的所有组件，
 * 包括 Agent 运行时链、模型路由、工具组装、流式处理、缓存等。
 * <p>
 * 仅在 classpath 中存在 LangChain4j 时生效。
 */
@AutoConfiguration
@AutoConfigureAfter(AstrsomnDatabaseAutoConfiguration.class)
@ConditionalOnClass(name = "dev.langchain4j.model.chat.ChatModel")
@ComponentScan(basePackages = "com.astrsomn.starter.runtime.langchain")
public class AstrsomnAiAutoConfiguration {
}
