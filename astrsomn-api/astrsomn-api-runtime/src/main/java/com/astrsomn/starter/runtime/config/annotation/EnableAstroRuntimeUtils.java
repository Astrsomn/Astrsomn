package com.astrsomn.starter.runtime.config.annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * Utility that scans the {@code BeanDefinitionRegistry} for a bean class
 * annotated with {@code @EnableAstroRuntime}.
 *
 * <p>This works during the PARSE_CONFIGURATION phase because the main
 * application class's {@code BeanDefinition} (an
 * {@code AnnotatedGenericBeanDefinition}) is already in the registry
 * when auto-configuration conditions are evaluated. The utility is
 * stateless — all state comes from the {@link ConditionContext} parameter.</p>
 */
public final class EnableAstroRuntimeUtils {

    private EnableAstroRuntimeUtils() {
    }

    /**
     * Returns {@code true} if any bean definition in the registry carries
     * {@code @EnableAstroRuntime}.
     */
    public static boolean isPresent(ConditionContext context) {
        return findAnnotationMetadata(context) != null;
    }

    /**
     * Reads a named attribute from the {@code @EnableAstroRuntime}
     * annotation. Returns {@code defaultValue} when the annotation is
     * absent or the attribute is not declared.
     *
     * @param context       condition evaluation context
     * @param attributeName annotation attribute name (e.g. "vector")
     * @param defaultValue  fallback when annotation/attribute is absent
     */
    public static boolean getAttribute(ConditionContext context,
                                        String attributeName,
                                        boolean defaultValue) {
        AnnotationMetadata metadata = findAnnotationMetadata(context);
        if (metadata == null) {
            return defaultValue;
        }
        Map<String, Object> attrs =
                metadata.getAnnotationAttributes(EnableAstroRuntime.class.getName());
        if (attrs == null) {
            return defaultValue;
        }
        Object value = attrs.get(attributeName);
        return value instanceof Boolean b ? b : defaultValue;
    }

    /**
     * Walks every {@code BeanDefinition} in the registry looking for one
     * whose {@code AnnotationMetadata} declares {@code @EnableAstroRuntime}.
     */
    private static AnnotationMetadata findAnnotationMetadata(ConditionContext context) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        if (beanFactory == null) {
            return null;
        }
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
            if (bd instanceof AnnotatedBeanDefinition abd) {
                AnnotationMetadata amd = abd.getMetadata();
                if (amd.hasAnnotation(EnableAstroRuntime.class.getName())) {
                    return amd;
                }
            }
        }
        return null;
    }
}
