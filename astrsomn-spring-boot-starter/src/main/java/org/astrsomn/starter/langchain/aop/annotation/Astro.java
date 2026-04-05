package org.astrsomn.starter.langchain.aop.annotation;


import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Astro {

    String agentKey() default "";

    String envCode() default "";

    String systemPromptKey() default "";

    boolean enableNetwork() default false;

    boolean enableDeepThinking() default false;

    boolean enableStream() default false;

}
