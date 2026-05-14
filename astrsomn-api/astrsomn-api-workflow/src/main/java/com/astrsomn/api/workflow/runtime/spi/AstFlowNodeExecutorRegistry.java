package com.astrsomn.api.workflow.runtime.spi;

public interface AstFlowNodeExecutorRegistry {

    AstFlowNodeExecutor resolve(String nodeType);
}