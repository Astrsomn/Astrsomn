package com.astrsomn.starter.langchain.runtime.chain;

import lombok.extern.slf4j.Slf4j;
import com.astrsomn.starter.context.EnvRuntime;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 按顺序执行所有 {@link AgentRuntimeChainHandler}，保证每一环都被调用到。
 */
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
