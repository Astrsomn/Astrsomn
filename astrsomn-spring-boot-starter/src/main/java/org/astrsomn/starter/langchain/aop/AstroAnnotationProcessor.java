package org.astrsomn.starter.langchain.aop;

import cn.hutool.core.bean.BeanException;
import jakarta.annotation.Resource;
import org.astrsomn.core.common.langchain.buildParam.AstroChatRequest;
import org.astrsomn.starter.langchain.AstroAssistantFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;



@Component
public class AstroAnnotationProcessor implements BeanPostProcessor {

    @Resource
    private AstroAssistantFactory assistantFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeanException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (field.isAnnotationPresent(Astro.class)) {

                ReflectionUtils.makeAccessible(field);
                Astro astro = field.getAnnotation(Astro.class);

                // 构建请求参数
                AstroChatRequest<?> request = AstroChatRequest.of(field.getType(), astro.agentKey());

                // 直接通过工厂创建实例（此时已经包含了 LangChain4j 的代理逻辑）
                Object assistant = assistantFactory.createAssistant(request);

                // 关键：注入到字段
                try {
                    field.set(bean, assistant);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("无法注入 @Astro 实例到字段: " + field.getName(), e);
                }
            }
        });
        return bean; // 必须返回 bean 本身
    }



}
