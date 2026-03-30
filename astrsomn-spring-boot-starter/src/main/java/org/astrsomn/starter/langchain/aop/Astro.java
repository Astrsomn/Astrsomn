package org.astrsomn.starter.langchain.aop;


import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Astro {


    /**
     * 业务 Agent key；可留空，此时使用配置 {@code astrsomn.refs.default-agent-key}（各环境 yml 填实际 key，避免改代码）。
     */
    String agentKey() default "";

    String envCode() default "";

    String systemPromptKey() default "";


    boolean enableNetwork() default false;


    boolean enableDeepThinking() default false;


    boolean enableStream() default false;

}
