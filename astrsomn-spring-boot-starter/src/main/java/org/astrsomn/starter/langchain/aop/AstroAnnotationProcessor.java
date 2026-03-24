package org.astrsomn.starter.langchain.aop;

import cn.hutool.core.bean.BeanException;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.langchain.buildParam.AstroChatRequest;
import org.astrsomn.starter.langchain.AstroAssistantFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;



@Component
@RequiredArgsConstructor
public class AstroAnnotationProcessor implements BeanPostProcessor {
    private final AstroAssistantFactory assistantFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeanException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (field.isAnnotationPresent(Astro.class)) {
                ReflectionUtils.makeAccessible(field);
                Astro astro = field.getAnnotation(Astro.class);
                AstroChatRequest<?> request = AstroChatRequest.of(field.getType(), astro.agentKey());
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



}
