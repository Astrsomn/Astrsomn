package com.astrsomn.starter.langchain.runtime;

import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.starter.config.AstrsomnProperties;
import com.astrsomn.starter.langchain.runtime.chain.AgentRuntimeChainExecutor;
import com.astrsomn.starter.langchain.runtime.chain.AgentRuntimeContext;
import org.springframework.stereotype.Component;

/**
 * 从库表加载 Agent → Instance → Model → Account 链路上的配置，并按链式处理器依次合并进 {@link AstroChatParam}。
 * <p>
 * 合并优先级：调用方在 Param 中已设置的项优先；未传 agentKey 时使用库中默认智能体（{@code IS_DEFAULT=1}），模型可再由实例或默认模型记录补齐。
 * <p>
 * 本链路不校验 {@code userMessage}；对话前应由业务层保证用户输入非空。
 */
@Component
@RequiredArgsConstructor
public class AgentRuntimeConfigLoader {

    private final AstrsomnProperties astrsomnProperties;
    private final AgentRuntimeChainExecutor agentRuntimeChainExecutor;

    public <T> void validateAndApplyAgent(AstroChatParam<T> param) {
        AgentRuntimeContext ctx = new AgentRuntimeContext(param, astrsomnProperties);
        agentRuntimeChainExecutor.execute(ctx);
    }
}
