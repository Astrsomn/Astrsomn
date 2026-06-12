package com.astrsomn.starter.runtime.config.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * Registrar triggered by {@code @EnableAstroRuntime @Import}.
 * Registers a marker bean and can register additional infrastructure
 * beans based on the annotation's subsystem attributes.
 *
 * <p>Runs during the REGISTER_BEAN phase — after auto-config conditions
 * are evaluated, so the marker bean is useful for downstream
 * {@code @ConditionalOnBean} checks rather than gating auto-config
 * itself (which is handled by {@link EnableAstroRuntimeUtils} during
 * the PARSE_CONFIGURATION phase).</p>
 */
public class EnableAstroRuntimeRegistrar implements ImportBeanDefinitionRegistrar {

    /** Marker bean name detectable by {@code @ConditionalOnBean}. */
    public static final String MARKER_BEAN_NAME = "enableAstroRuntimeMarker";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                         BeanDefinitionRegistry registry) {
        Map<String, Object> attrs = importingClassMetadata
                .getAnnotationAttributes(EnableAstroRuntime.class.getName());
        if (attrs == null) {
            return;
        }

        // Register a lightweight marker bean so other components can
        // detect via @ConditionalOnBean that the runtime was enabled
        // through the annotation (as opposed to YAML).
        if (!registry.containsBeanDefinition(MARKER_BEAN_NAME)) {
            GenericBeanDefinition markerDef = new GenericBeanDefinition();
            markerDef.setBeanClass(EnableAstroRuntimeMarker.class);
            registry.registerBeanDefinition(MARKER_BEAN_NAME, markerDef);
        }

        // Future extension point: register subsystem-specific
        // infrastructure beans based on attrs.get("vector"),
        // attrs.get("workflow"), attrs.get("system").
    }

    /**
     * Marker class registered as a bean when {@code @EnableAstroRuntime}
     * is active on the importing configuration class.
     */
    static final class EnableAstroRuntimeMarker {
    }
}
