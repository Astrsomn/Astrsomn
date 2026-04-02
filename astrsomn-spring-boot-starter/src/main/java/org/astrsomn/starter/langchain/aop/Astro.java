package org.astrsomn.starter.langchain.aop;


import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Astro {


    /**
     * 业务 Agent key；可留空，此时使用当前环境下 {@code AI_AGENT.IS_DEFAULT = 1} 对应记录的 {@code AGENT_KEY}。
     */
    String agentKey() default "";

    String envCode() default "";

    String systemPromptKey() default "";


    boolean enableNetwork() default false;


    boolean enableDeepThinking() default false;


    boolean enableStream() default false;

}
