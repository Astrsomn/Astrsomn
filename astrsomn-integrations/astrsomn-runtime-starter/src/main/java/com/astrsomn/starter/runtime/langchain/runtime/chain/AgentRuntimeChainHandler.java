package com.astrsomn.starter.runtime.langchain.runtime.chain;


@FunctionalInterface
public interface AgentRuntimeChainHandler {

    void handle(AgentRuntimeContext ctx);
}
