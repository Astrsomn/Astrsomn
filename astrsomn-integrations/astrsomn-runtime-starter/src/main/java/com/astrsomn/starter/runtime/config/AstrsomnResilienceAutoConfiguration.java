package com.astrsomn.starter.runtime.config;

import com.astrsomn.starter.runtime.langchain.route.ResilienceDecorationStrategy;
import com.astrsomn.starter.runtime.langchain.route.resilience.RegistryResilienceDecorationStrategy;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.Optional;


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
