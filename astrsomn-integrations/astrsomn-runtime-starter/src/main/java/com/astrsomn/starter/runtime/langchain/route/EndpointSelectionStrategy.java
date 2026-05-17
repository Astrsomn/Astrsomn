package com.astrsomn.starter.runtime.langchain.route;

import java.util.Set;

/**
 * 多节点负载：在候选端点下标中选择其一；可结合 {@code excluded} 做故障转移。
 */
public interface EndpointSelectionStrategy {

    String getId();

    /**
     * @return 选中的下标，或 -1 表示无可选节点
     */
    int select(ModelRouteSelectionInput input, Set<Integer> excluded);
}
