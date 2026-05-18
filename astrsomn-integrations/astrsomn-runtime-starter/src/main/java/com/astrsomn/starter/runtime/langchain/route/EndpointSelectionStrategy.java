package com.astrsomn.starter.runtime.langchain.route;

import java.util.Set;


public interface EndpointSelectionStrategy {

    String getId();

    
    int select(ModelRouteSelectionInput input, Set<Integer> excluded);
}
