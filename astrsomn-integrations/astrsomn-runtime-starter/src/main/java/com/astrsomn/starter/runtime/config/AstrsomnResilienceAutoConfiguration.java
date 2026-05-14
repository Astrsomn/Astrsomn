package com.astrsomn.starter.runtime.config;

import com.astrsomn.starter.runtime.langchain.route.resilience.RegistryResilienceDecorationStrategy;
import com.astrsomn.starter.runtime.langchain.route.ResilienceDecorationStrategy;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

/**
 * 当 classpath 存在 Resilience4j 且宿主注册了 {@link CircuitBreakerRegistry} 时，注册 {@code registry} 韧性策略。
 */
@AutoConfiguration
@ConditionalOnClass(name = "io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry")
public class AstrsomnResilienceAutoConfiguration {

    @Bean
    @ConditionalOnBean(CircuitBreakerRegistry.class)
    public ResilienceDecorationStrategy registryResilienceDecorationStrategy(
            CircuitBreakerRegistry circuitBreakerRegistry,
            Optional<RetryRegistry> retryRegistry) {
        return new RegistryResilienceDecorationStrategy(circuitBreakerRegistry, retryRegistry);
    }
}
