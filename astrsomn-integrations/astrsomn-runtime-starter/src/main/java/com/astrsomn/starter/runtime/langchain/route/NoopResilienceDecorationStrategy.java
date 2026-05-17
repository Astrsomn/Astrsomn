package com.astrsomn.starter.runtime.langchain.route;

import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelRouteSetting;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class NoopResilienceDecorationStrategy implements ResilienceDecorationStrategy {

    @Override
    public String getId() {
        return "noop";
    }

    @Override
    public <T> T decorate(Supplier<T> supplier, ModelRouteContext ctx, int endpointIndex, ModelRouteSetting route) {
        return supplier.get();
    }
}
