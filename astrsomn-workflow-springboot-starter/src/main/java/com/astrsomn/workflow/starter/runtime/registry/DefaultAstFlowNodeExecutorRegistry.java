package com.astrsomn.workflow.starter.runtime.registry;

import com.astrsomn.workflow.core.runtime.spi.AstFlowNodeExecutor;
import com.astrsomn.workflow.core.runtime.spi.AstFlowNodeExecutorRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultAstFlowNodeExecutorRegistry implements AstFlowNodeExecutorRegistry {

    private final Map<String, AstFlowNodeExecutor> executors = new HashMap<>();

    public DefaultAstFlowNodeExecutorRegistry(List<AstFlowNodeExecutor> executorList) {
        for (AstFlowNodeExecutor executor : executorList) {
            AstFlowNodeExecutor previous = executors.putIfAbsent(executor.type(), executor);
            if (previous != null) {
                throw new IllegalStateException("Duplicate node executor type: " + executor.type());
            }
        }
    }

    @Override
    public AstFlowNodeExecutor resolve(String nodeType) {
        AstFlowNodeExecutor executor = executors.get(nodeType);
        if (executor == null) {
            throw new IllegalArgumentException("No node executor found for type: " + nodeType);
        }
        return executor;
    }
}
