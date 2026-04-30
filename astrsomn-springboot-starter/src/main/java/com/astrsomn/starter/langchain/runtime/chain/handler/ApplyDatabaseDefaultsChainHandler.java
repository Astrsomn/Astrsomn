package com.astrsomn.starter.langchain.runtime.chain.handler;

import com.astrsomn.starter.langchain.runtime.chain.AgentRuntimeChainHandler;
import com.astrsomn.starter.langchain.runtime.chain.AgentRuntimeContext;
import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.starter.langchain.runtime.AiRuntimeDefaultsResolver;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 未传 {@code agentKey} 时，使用当前环境下 {@code AI_AGENT.IS_DEFAULT = 1} 的记录，保证有一条入口链路。
 */
@Component
@Order(10)
@RequiredArgsConstructor
public class ApplyDatabaseDefaultsChainHandler implements AgentRuntimeChainHandler {

    private final AiRuntimeDefaultsResolver aiRuntimeDefaultsResolver;

    @Override
    public void handle(AgentRuntimeContext ctx) {
        if (StringUtils.isBlank(ctx.getParam().getAgentKey())) {
            aiRuntimeDefaultsResolver
                    .resolveDefaultAgentKey(ctx.getEnvCode())
                    .ifPresent(key -> ctx.getParam().setAgentKey(key));
        }
    }
}
