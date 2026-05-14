package com.astrsomn.starter.runtime.langchain.route;

import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 按配置顺序依次尝试：第 {@code attemptIndex} 次故障转移优先选下一顺位可用端点。
 */
@Component
public class FailoverOrderedEndpointSelectionStrategy implements EndpointSelectionStrategy {

    @Override
    public String getId() {
        return "failoverOrdered";
    }

    @Override
    public int select(ModelRouteSelectionInput input, Set<Integer> excluded) {
        int n = input.size();
        if (n <= 0) {
            return -1;
        }
        int start = Math.floorMod(input.attemptIndex(), n);
        for (int i = 0; i < n; i++) {
            int idx = (start + i) % n;
            if (!excluded.contains(idx)) {
                return idx;
            }
        }
        return -1;
    }
}
