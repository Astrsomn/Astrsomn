package com.astrsomn.starter.runtime.config.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enables the Astrsomn runtime and its subsystems via annotation, replacing
 * the {@code astrsomn.enabled: true} YAML property.
 *
 * <p>Place on the main {@code @SpringBootApplication} class.
 * Each subsystem attribute defaults to {@code true} — set any to
 * {@code false} to skip that subsystem's auto-configuration.</p>
 *
 * <p><b>Backward compatibility:</b> when this annotation is absent, the
 * runtime falls back to the {@code astrsomn.enabled} environment property.</p>
 *
 * <pre>
 * &#064;EnableAstroRuntime                      // all subsystems on
 * &#064;EnableAstroRuntime(vector = false)       // skip vector store
 * &#064;EnableAstroRuntime(workflow = false)     // skip workflow engine
 * </pre>
 *
 * @see EnableAstroRuntimeRegistrar
 * @see EnableAstroRuntimeUtils
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableAstroRuntimeRegistrar.class)
public @interface EnableAstroRuntime {

    /** Enable vector-store auto-configuration. Default {@code true}. */
    boolean vector() default true;

    /** Enable workflow-engine auto-configuration. Default {@code true}. */
    boolean workflow() default true;

    /** Enable system-management auto-configuration. Default {@code true}. */
    boolean system() default true;
}
