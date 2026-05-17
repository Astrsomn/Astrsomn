package com.astrsomn.starter.runtime.langchain.runtime;

import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeChainExecutor;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 从库表加载 Agent → Instance → Model → Account 链路上的配置，并按链式处理器依次合并进 {@link AstroChatParam}。
 * <p>
 * 合并优先级：调用方在 Param 中已设置的项优先；未传 agentKey 时使用库中默认智能体（{@code IS_DEFAULT=1}），模型可再由实例或默认模型记录补齐。
 * <p>
 * 本链路不强制校验用户输入字段；对话前应由业务层保证可解析的用户消息非空。
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
