package org.astrsomn.starter.langchain.aop;

import cn.hutool.core.bean.BeanException;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.langchain.factory.AstroAssistantFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;



@Component
@RequiredArgsConstructor
public class AstroAnnotationProcessor implements BeanPostProcessor {
    private final AstroAssistantFactory assistantFactory;
    private final AstrsomnProperties astrsomnProperties;

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
     * 注解优先；未配置时使用 {@link AstrsomnProperties#getRefs()} {@link AstrsomnProperties.Refs#getDefaultAgentKey()}。
     */
    private String resolveAgentKey(Astro astro) {
        if (StringUtils.isNotBlank(astro.agentKey())) {
            return astro.agentKey().trim();
        }
        AstrsomnProperties.Refs refs = astrsomnProperties.getRefs();
        if (refs != null && StringUtils.isNotBlank(refs.getDefaultAgentKey())) {
            return refs.getDefaultAgentKey().trim();
        }
        throw new IllegalStateException(
                "@Astro 未指定 agentKey，且未配置 astrsomn.refs.default-agent-key（请在 yml 中按环境填写）");
    }
}
