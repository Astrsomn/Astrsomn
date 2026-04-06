package org.astrsomn.starter.langchain.aop.processor;

import cn.hutool.core.bean.BeanException;
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

/**
 * 作用：处理Astro注解，注入Assistant实例
 */
@Component
@RequiredArgsConstructor
public class AstroAnnotationProcessor implements BeanPostProcessor {
    private final AstroAssistantFactory assistantFactory;
    private final AstrsomnProperties astrsomnProperties;
    private final AiRuntimeDefaultsResolver aiRuntimeDefaultsResolver;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeanException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (field.isAnnotationPresent(Astro.class)) {
                ReflectionUtils.makeAccessible(field);
                Astro astro = field.getAnnotation(Astro.class);
                String agentKey = resolveAgentKey(astro);
                AstroChatParam<?> request = AstroChatParam.of(field.getType(), agentKey);
                Object assistant = assistantFactory.createAssistant(request);
                try {
                    field.set(bean, assistant);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("无法注入 @Astro 实例到字段: " + field.getName(), e);
                }
            }
        });
        return bean;
    }

    /**
     * 注解优先；未指定时使用当前环境下 {@code AI_AGENT.IS_DEFAULT = 1} 的 {@code AGENT_KEY}。
     */
    private String resolveAgentKey(Astro astro) {
        if (StringUtils.isNotBlank(astro.agentKey())) {
            return astro.agentKey().trim();
        }
        String envCode = EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties);
        return aiRuntimeDefaultsResolver
                .resolveDefaultAgentKey(envCode)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "@Astro 未指定 agentKey，且库中不存在当前环境(envCode="
                                                + envCode
                                                + ")下 IS_DEFAULT=1 的 AI_AGENT 记录"));
    }
}
