package com.astrsomn.starter.runtime.langchain.route;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomEndpointSelectionStrategy implements EndpointSelectionStrategy {

    @Override
    public String getId() {
        return "random";
    }

    @Override
    public int select(ModelRouteSelectionInput input, Set<Integer> excluded) {
        int n = input.size();
        if (n <= 0) {
            return -1;
        }
        List<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!excluded.contains(i)) {
                candidates.add(i);
            }
        }
        if (candidates.isEmpty()) {
            return -1;
        }
        Collections.shuffle(candidates, ThreadLocalRandom.current());
        return candidates.get(0);
    }
}
