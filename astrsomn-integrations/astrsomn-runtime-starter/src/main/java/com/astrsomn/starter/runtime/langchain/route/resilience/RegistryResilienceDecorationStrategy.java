package com.astrsomn.starter.runtime.langchain.route.resilience;

import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelRouteSetting;
import com.astrsomn.starter.runtime.langchain.route.ModelRouteContext;
import com.astrsomn.starter.runtime.langchain.route.ResilienceDecorationStrategy;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;

import java.util.Optional;
import java.util.function.Supplier;


public class RegistryResilienceDecorationStrategy implements ResilienceDecorationStrategy {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final Optional<RetryRegistry> retryRegistry;

    public RegistryResilienceDecorationStrategy(
            CircuitBreakerRegistry circuitBreakerRegistry,
            Optional<RetryRegistry> retryRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
    }

    @Override
    public String getId() {
        return "registry";
    }

    @Override
    public <T> T decorate(Supplier<T> supplier, ModelRouteContext ctx, int endpointIndex, ModelRouteSetting route) {
        String name = ResilienceInstanceNames.resolve(route, ctx, endpointIndex);
        Supplier<T> decorated = supplier;
        Optional<CircuitBreaker> cb = circuitBreakerRegistry.find(name);
        if (cb.isPresent()) {
            decorated = CircuitBreaker.decorateSupplier(cb.get(), decorated);
        }
        if (retryRegistry.isPresent()) {
            Optional<Retry> retry = retryRegistry.get().find(name);
            if (retry.isPresent()) {
                decorated = Retry.decorateSupplier(retry.get(), decorated);
            }
        }
        return decorated.get();
    }
}
