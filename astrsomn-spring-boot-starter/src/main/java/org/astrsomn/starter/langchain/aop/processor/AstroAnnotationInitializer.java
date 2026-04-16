package org.astrsomn.starter.langchain.aop.processor;


import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.context.EnvRuntime;
import org.astrsomn.starter.langchain.aop.annotation.Astro;
import org.astrsomn.starter.langchain.factory.AstroAssistantFactory;
import org.astrsomn.starter.langchain.runtime.AiRuntimeDefaultsResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;

import java.util.Optional;

/**
 * 负责解析并注入 @Astro 注解标记的 AI Assistant 实例。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AstroAnnotationInitializer implements BeanPostProcessor {

    private static final String LOG_PREFIX = "[Astrsomn] [AI注入器] ====> ";

    private final AstroAssistantFactory assistantFactory;
    private final AstrsomnProperties astrsomnProperties;
    private final AiRuntimeDefaultsResolver aiDefaultsResolver;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (!field.isAnnotationPresent(Astro.class)) return;

            ReflectionUtils.makeAccessible(field);
            Astro astro = field.getAnnotation(Astro.class);
            String agentKey = resolveAgentKey(astro);

            Object assistant = assistantFactory.createAssistant(AstroChatParam.of(field.getType(), agentKey));

            try {
                field.set(bean, assistant);
                log.debug("{} 成功注入 Assistant | Bean: {} | Field: {} | AgentKey: {}",
                        LOG_PREFIX, beanName, field.getName(), agentKey);
            } catch (IllegalAccessException e) {
                log.error("{} 注入失败 | 字段: {} | 错误: {}", LOG_PREFIX, field.getName(), e.getMessage());
                throw new RuntimeException("Injection failed for @Astro field: " + field.getName(), e);
            }
        });
        return bean;
    }

    /**
     * 解析 AgentKey：注解优先，缺省则查找环境下默认 Agent
     */
    private String resolveAgentKey(Astro astro) {
        return Optional.ofNullable(StringUtils.trimToNull(astro.agentKey()))
                .or(() -> {
                    String env = EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties);
                    return aiDefaultsResolver.resolveDefaultAgentKey(env);
                })
                .orElseThrow(() -> {
                    String env = EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties);
                    return new IllegalStateException(LOG_PREFIX + "无法解析 AgentKey：注解未指定且环境 [" + env + "] 无默认配置");
                });
    }
}
