package com.astrsomn.starter.runtime.langchain.route;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ResilienceDecorationStrategyResolver {

    private final Map<String, ResilienceDecorationStrategy> strategies = new HashMap<>();

    public ResilienceDecorationStrategyResolver(List<ResilienceDecorationStrategy> strategiesList) {
        for (ResilienceDecorationStrategy s : strategiesList) {
            strategies.put(s.getId(), s);
        }
    }

    public ResilienceDecorationStrategy resolve(String id) {
        if (id == null || id.isBlank()) {
            return strategies.getOrDefault("noop", new NoopResilienceDecorationStrategy());
        }
        ResilienceDecorationStrategy s = strategies.get(id);
        if (s != null) {
            return s;
        }
        log.warn("[Astro] Unknown resilience decoration strategy '{}', using noop", id);
        return strategies.getOrDefault("noop", new NoopResilienceDecorationStrategy());
    }
}
