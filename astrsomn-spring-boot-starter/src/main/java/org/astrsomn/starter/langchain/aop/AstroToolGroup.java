package org.astrsomn.starter.langchain.aop;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface AstroToolGroup {

    String value() default "";

    String description() default "";

    String type() default "method";
}