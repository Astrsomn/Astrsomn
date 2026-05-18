package com.astrsomn.starter.runtime.langchain.runtime.chain.handler;

import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.langchain.runtime.AiRuntimeDefaultsResolver;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeChainHandler;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeContext;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


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
