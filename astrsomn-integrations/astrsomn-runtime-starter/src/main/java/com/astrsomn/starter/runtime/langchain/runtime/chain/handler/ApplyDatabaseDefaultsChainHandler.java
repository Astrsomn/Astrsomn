package com.astrsomn.starter.runtime.langchain.runtime.chain.handler;

import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.langchain.runtime.AiRuntimeDefaultsResolver;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeChainHandler;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeContext;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 未传 {@code bizKey} 时，使用当前环境下 {@code AI_AGENT.IS_DEFAULT = 1} 的记录，保证有一条入口链路。
 */
@Component
@Order(10)
@RequiredArgsConstructor
public class ApplyDatabaseDefaultsChainHandler implements AgentRuntimeChainHandler {

    private final AiRuntimeDefaultsResolver aiRuntimeDefaultsResolver;

    @Override
    public void handle(AgentRuntimeContext ctx) {
        if (StringUtils.isBlank(ctx.getParam().getBizKey())) {
            aiRuntimeDefaultsResolver
                    .resolveDefaultBizKey(ctx.getEnvCode())
                    .ifPresent(key -> ctx.getParam().setBizKey(key));
        }
    }
}
