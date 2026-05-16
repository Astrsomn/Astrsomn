package com.astrsomn.starter.runtime.langchain.route;

import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

/**
 * 按 memoryKey 与 agentKey 哈希粘性到某一端点；若该端点不可用则在环上向后探测。
 */
@Component
public class StickyMemoryEndpointSelectionStrategy implements EndpointSelectionStrategy {

    @Override
    public String getId() {
        return "stickyMemory";
    }

    @Override
    public int select(ModelRouteSelectionInput input, Set<Integer> excluded) {
        int n = input.size();
        if (n <= 0) {
            return -1;
        }
        ModelRouteContext ctx = input.context();
        int base = Math.floorMod(
                Objects.hash(
                        ctx.memoryKey() != null ? ctx.memoryKey() : "",
                        ctx.agentKey() != null ? ctx.agentKey() : ""),
                n);
        for (int i = 0; i < n; i++) {
            int idx = (base + i) % n;
            if (!excluded.contains(idx)) {
                return idx;
            }
        }
        return -1;
    }
}
