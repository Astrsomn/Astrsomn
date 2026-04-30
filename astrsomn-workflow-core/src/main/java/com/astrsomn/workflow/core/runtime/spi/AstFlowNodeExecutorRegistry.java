package com.astrsomn.workflow.core.runtime.spi;

public interface AstFlowNodeExecutorRegistry {

    AstFlowNodeExecutor resolve(String nodeType);
}
