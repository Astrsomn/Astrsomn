package com.astrsomn.starter.runtime.langchain.aop.processor;


import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.context.EnvRuntime;
import com.astrsomn.starter.runtime.langchain.aop.annotation.Astro;
import com.astrsomn.starter.runtime.langchain.factory.AstroAssistantFactory;
import com.astrsomn.starter.runtime.langchain.runtime.AiRuntimeDefaultsResolver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 负责解析并注入 @Astro 注解标记的 AI Assistant 实例。
 */
@Slf4j
@Component
public class AstroAnnotationInitializer implements BeanPostProcessor, PriorityOrdered, BeanFactoryAware {

    private static final String LOG_PREFIX = "[Astrsomn] [AI注入器] ====> ";

    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        log.debug("{} 开始处理 Bean | BeanName: {} | BeanClass: {}",
                LOG_PREFIX, beanName, bean.getClass().getName());

        Class<?> targetClass = ClassUtils.getUserClass(bean);

        final boolean[] hasAstroField = {false};
        ReflectionUtils.doWithFields(targetClass, field -> {
            if (!field.isAnnotationPresent(Astro.class)) return;
            
            hasAstroField[0] = true;
            log.info("{} 发现 @Astro 注解字段 | Bean: {} | Field: {} | FieldType: {}",
                    LOG_PREFIX, beanName, field.getName(), field.getType().getName());

            ReflectionUtils.makeAccessible(field);
            Astro astro = field.getAnnotation(Astro.class);
            
            log.debug("{} 解析注解参数 | Bean: {} | Field: {} | agentKey: {} | envCode: {} | promptKey: {}",
                    LOG_PREFIX, beanName, field.getName(), astro.agentKey(), astro.envCode(), astro.promptKey());

            String agentKey = resolveAgentKey(astro);
            log.debug("{} 解析后的 AgentKey | Bean: {} | Field: {} | AgentKey: {}",
                    LOG_PREFIX, beanName, field.getName(), agentKey);

            Object assistant = null;
            try {
                if (!field.getType().isInterface()) {
                    log.warn("{} 字段类型不是接口，降级为启动时实例化 | Bean: {} | Field: {} | Type: {}",
                            LOG_PREFIX, beanName, field.getName(), field.getType().getName());
                    assistant = createAssistantNow(field.getType(), agentKey, beanName, field.getName());
                } else {
                    assistant = createLazyAssistantProxy(field.getType(), agentKey, beanName, field.getName());
                }
                log.info("{} 注入延迟 Assistant 代理成功 | Bean: {} | Field: {} | Assistant: {}",
                        LOG_PREFIX, beanName, field.getName(), assistant.getClass().getName());
            } catch (Exception e) {
                log.error("{} Assistant 创建失败 | Bean: {} | Field: {} | 错误: {}",
                        LOG_PREFIX, beanName, field.getName(), e.getMessage(), e);
                throw e;
            }

            try {
                field.set(bean, assistant);
                log.info("{} 成功注入 Assistant | Bean: {} | Field: {} | AgentKey: {}",
                        LOG_PREFIX, beanName, field.getName(), agentKey);
            } catch (IllegalAccessException e) {
                log.error("{} 注入失败 | Bean: {} | Field: {} | 错误: {}",
                        LOG_PREFIX, beanName, field.getName(), e.getMessage(), e);
                throw new RuntimeException("Injection failed for @Astro field: " + field.getName(), e);
            }
        });
        
        if (!hasAstroField[0]) {
            log.debug("{} Bean 无 @Astro 注解字段 | BeanName: {}", LOG_PREFIX, beanName);
        }
        
        return bean;
    }

    @Override
    public int getOrder() {
        // Run late among regular BPPs to reduce side effects on early infra bean creation.
        return PriorityOrdered.LOWEST_PRECEDENCE;
    }

    /**
     * 解析 AgentKey：注解优先，缺省则查找环境下默认 Agent
     */
    private String resolveAgentKey(Astro astro) {
        String annotationAgentKey = StringUtils.trimToNull(astro.agentKey());
        log.debug("{} 解析 AgentKey | 注解 agentKey: {}", LOG_PREFIX, annotationAgentKey);
        
        if (annotationAgentKey != null) {
            log.debug("{} 使用注解中的 agentKey: {}", LOG_PREFIX, annotationAgentKey);
            return annotationAgentKey;
        }
        
        String env = EnvRuntime.resolveEffectiveEnvCode(beanFactory.getBean(AstrsomnProperties.class));
        log.debug("{} 注解未指定 agentKey，尝试从环境 [{}] 查询默认 Agent", LOG_PREFIX, env);
        
        Optional<String> defaultAgentKey = beanFactory.getBean(AiRuntimeDefaultsResolver.class).resolveDefaultAgentKey(env);
        
        if (defaultAgentKey.isPresent()) {
            log.debug("{} 从数据库查询到默认 AgentKey: {}", LOG_PREFIX, defaultAgentKey.get());
            return defaultAgentKey.get();
        }
        
        log.error("{} 无法解析 AgentKey：注解未指定且环境 [{}] 无默认配置", LOG_PREFIX, env);
        throw new IllegalStateException(LOG_PREFIX + "无法解析 AgentKey：注解未指定且环境 [" + env + "] 无默认配置");
    }

    private Object createLazyAssistantProxy(Class<?> serviceClass, String agentKey, String beanName, String fieldName) {
        AtomicReference<Object> delegateRef = new AtomicReference<>();
        Object proxy = Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass},
                (p, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        String name = method.getName();
                        if ("toString".equals(name)) {
                            return "LazyAstroAssistantProxy(" + serviceClass.getSimpleName() + "," + agentKey + ")";
                        }
                        if ("hashCode".equals(name)) {
                            return System.identityHashCode(p);
                        }
                        if ("equals".equals(name)) {
                            return p == args[0];
                        }
                        return null;
                    }
                    Object delegate = delegateRef.get();
                    if (delegate == null) {
                        synchronized (delegateRef) {
                            delegate = delegateRef.get();
                            if (delegate == null) {
                                delegate = createAssistantNow(serviceClass, agentKey, beanName, fieldName);
                                delegateRef.set(delegate);
                                log.info("{} 首次调用触发 Assistant 实例化 | Bean: {} | Field: {} | AgentKey: {}",
                                        LOG_PREFIX, beanName, fieldName, agentKey);
                            }
                        }
                    }
                    try {
                        return method.invoke(delegate, args);
                    } catch (InvocationTargetException ex) {
                        throw ex.getTargetException();
                    }
                }
        );
        return proxy;
    }

    private Object createAssistantNow(Class<?> serviceClass, String agentKey, String beanName, String fieldName) {
        log.debug("{} 开始创建 Assistant | Bean: {} | Field: {} | ServiceClass: {}",
                LOG_PREFIX, beanName, fieldName, serviceClass.getName());
        Object assistant = beanFactory.getBeanProvider(AstroAssistantFactory.class)
                .getObject()
                .createAssistant(AstroChatParam.of(serviceClass, agentKey));
        if (assistant == null) {
            throw new IllegalStateException("assistantFactory returned null for field: " + fieldName);
        }
        log.info("{} Assistant 创建成功 | Bean: {} | Field: {} | Assistant: {}",
                LOG_PREFIX, beanName, fieldName, assistant.getClass().getName());
        return assistant;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
