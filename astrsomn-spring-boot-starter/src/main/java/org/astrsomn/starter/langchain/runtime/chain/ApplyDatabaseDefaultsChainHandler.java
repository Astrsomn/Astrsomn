package org.astrsomn.starter.langchain.runtime.chain;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.starter.langchain.runtime.AiRuntimeDefaultsResolver;
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
