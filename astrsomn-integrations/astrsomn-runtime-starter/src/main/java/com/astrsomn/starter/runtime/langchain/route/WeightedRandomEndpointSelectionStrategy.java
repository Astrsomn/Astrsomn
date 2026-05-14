package com.astrsomn.starter.runtime.langchain.route;

import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelEndpoint;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class WeightedRandomEndpointSelectionStrategy implements EndpointSelectionStrategy {

    @Override
    public String getId() {
        return "weightedRandom";
    }

    @Override
    public int select(ModelRouteSelectionInput input, Set<Integer> excluded) {
        List<ModelEndpoint> endpoints = input.endpoints();
        if (endpoints == null || endpoints.isEmpty()) {
            return -1;
        }
        int total = 0;
        for (int i = 0; i < endpoints.size(); i++) {
            if (excluded.contains(i)) {
                continue;
            }
            int w = weight(endpoints.get(i));
            if (w > 0) {
                total += w;
            }
        }
        if (total <= 0) {
            return -1;
        }
        int r = ThreadLocalRandom.current().nextInt(total);
        for (int i = 0; i < endpoints.size(); i++) {
            if (excluded.contains(i)) {
                continue;
            }
            int w = weight(endpoints.get(i));
            if (w <= 0) {
                continue;
            }
            r -= w;
            if (r < 0) {
                return i;
            }
        }
        return -1;
    }

    private static int weight(ModelEndpoint e) {
        Integer w = e.getWeight();
        return w == null || w < 1 ? 1 : w;
    }
}
