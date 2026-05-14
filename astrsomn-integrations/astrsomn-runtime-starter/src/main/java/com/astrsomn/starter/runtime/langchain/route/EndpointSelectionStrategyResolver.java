package com.astrsomn.starter.runtime.langchain.route;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EndpointSelectionStrategyResolver {

    private final Map<String, EndpointSelectionStrategy> strategies = new HashMap<>();

    public EndpointSelectionStrategyResolver(List<EndpointSelectionStrategy> strategiesList) {
        for (EndpointSelectionStrategy s : strategiesList) {
            EndpointSelectionStrategy prev = strategies.put(s.getId(), s);
            if (prev != null) {
                log.warn("[Astro] Duplicate endpoint selection strategy id '{}', keeping {}", s.getId(), s.getClass().getName());
            }
        }
        if (!strategies.containsKey("roundRobin")) {
            strategies.put("roundRobin", new RoundRobinEndpointSelectionStrategy());
        }
    }

    public EndpointSelectionStrategy resolve(String id) {
        if (id == null || id.isBlank()) {
            return strategies.get("roundRobin");
        }
        EndpointSelectionStrategy s = strategies.get(id);
        if (s != null) {
            return s;
        }
        log.warn("[Astro] Unknown endpoint selection strategy '{}', fallback to roundRobin", id);
        return strategies.get("roundRobin");
    }
}
