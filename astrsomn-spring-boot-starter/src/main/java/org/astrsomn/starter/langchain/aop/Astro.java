package org.astrsomn.starter.langchain.aop;


import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Astro {

    String agentKey() default "";

    String envCode() default "";

    double temperature() default 0.0;
}
