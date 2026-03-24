package org.astrsomn.starter.langchain.aop;

import cn.hutool.core.bean.BeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static java.lang.reflect.Proxy.newProxyInstance;

@Component
public class AstroAnnotationProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeanException{
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (field.isAnnotationPresent(Astro.class)) {

                ReflectionUtils.makeAccessible(field);

                Astro astro = field.getAnnotation(Astro.class);

                String agentKey = astro.agentKey();
                String envCode = astro.envCode();


                // 3. 获取字段类型 (例如 OrderCreateAssistant)
                Class<?> fieldType = field.getType();

                // 4. 核心逻辑：根据注解参数创建实例
                // 场景 A: 如果 fieldType 是接口，通常创建动态代理
                // 场景 B: 如果 fieldType 是具体类，直接 new 或者从上下文获取
                Object proxyInstance = createAstroInstance(fieldType, agentKey, envCode);

            }
        });
        return bean; // 必须返回 bean 本身
    }

    private Object createAstroInstance(Class<?> type, String agentKey, String envCode) {
        // --- 示例逻辑：如果是接口，返回一个动态代理 ---
        if (type.isInterface()) {
            return newProxyInstance(
                    type.getClassLoader(),
                    new Class<?>[]{type},
                    (proxy, method, args) -> {
                        // 这里是方法调用的拦截点
                        System.out.println(">>> 调用 AI Agent: " + agentKey +
                                ", 方法: " + method.getName() );

                        // TODO: 在这里构建 AstroChatRequest 并调用后端 API
                        // AstroChatRequest req = AstroChatRequest.of(...)
                        // return apiClient.chat(req, args);

                        // 模拟返回
                        return null;
                    }
            );
        } else {
            // 如果是具体类，尝试无参构造实例化，或者抛出异常提示用户只能注入接口
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("无法实例化 @Astro 标注的非接口类: " + type.getName(), e);
            }
        }
    }


}
