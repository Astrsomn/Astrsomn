package com.astrsomn.starter.runtime.langchain.runtime.chain;

/**
 * Agent 运行时配置链中的一环；按 Spring {@code @Order} 升序执行。
 * <p>
 * 合并语义：对 {@link AgentRuntimeContext#getParam()} 仅做「目标为 null（或集合为空）时从本层补齐」，
 * 以保证调用方传入的创建时配置始终优先于业务表与启动默认项。
 */
@FunctionalInterface
public interface AgentRuntimeChainHandler {

    void handle(AgentRuntimeContext ctx);
}
