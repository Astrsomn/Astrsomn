package com.astrsomn.starter.runtime.langchain.runtime.chain;

import com.astrsomn.starter.runtime.context.EnvRuntime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class AgentRuntimeChainExecutor {

    private final List<AgentRuntimeChainHandler> handlers;

    public AgentRuntimeChainExecutor(List<AgentRuntimeChainHandler> handlerBeans) {
        this.handlers = new ArrayList<>(handlerBeans);
        AnnotationAwareOrderComparator.sort(this.handlers);
    }

    public void execute(AgentRuntimeContext ctx) {
        ctx.setEnvCode(EnvRuntime.resolveEffectiveEnvCode(ctx.getProperties()));
        for (AgentRuntimeChainHandler handler : handlers) {
            if (log.isDebugEnabled()) {
                log.debug("Agent runtime chain: {}", handler.getClass().getSimpleName());
            }
            handler.handle(ctx);
        }
    }
}
