package com.astrsomn.starter.runtime.system.config;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(EnableAstroSystemCondition.class)
public class AstrsomnSystemAutoConfiguration {


}
