package com.astrsomn.starter.runtime.config;

import com.astrsomn.starter.runtime.config.datasource.AstrsomnDatabaseAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;

/**
 * Astrsomn runtime-starter 总入口。
 * <p>
 * 按职责编排两个子配置：
 * <ul>
 *   <li>{@link AstrsomnDatabaseAutoConfiguration} — 数据库基础设施</li>
 *   <li>{@link AstrsomnAiAutoConfiguration} — AI 编排层</li>
 * </ul>
 */
@AutoConfiguration
@AutoConfigureAfter(AstrsomnPropertiesAutoConfiguration.class)
@Conditional(AstrsomnStarterRuntimeCondition.class)
@Import({AstrsomnDatabaseAutoConfiguration.class, AstrsomnAiAutoConfiguration.class})
public class AstrsomnAutoConfiguration {
}
