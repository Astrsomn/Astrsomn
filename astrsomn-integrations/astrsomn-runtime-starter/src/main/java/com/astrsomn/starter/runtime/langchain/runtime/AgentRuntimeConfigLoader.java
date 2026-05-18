package com.astrsomn.starter.runtime.langchain.runtime;

import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeChainExecutor;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


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
