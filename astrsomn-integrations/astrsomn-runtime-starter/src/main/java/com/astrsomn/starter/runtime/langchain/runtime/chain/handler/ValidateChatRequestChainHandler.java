package com.astrsomn.starter.runtime.langchain.runtime.chain.handler;

import cn.hutool.core.lang.UUID;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.langchain.exception.AstroConfigException;
import com.astrsomn.starter.runtime.langchain.exception.ErrorCode;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeChainHandler;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(20)
public class ValidateChatRequestChainHandler implements AgentRuntimeChainHandler {

    @Override
    public void handle(AgentRuntimeContext ctx) {
        if (StringUtils.isBlank(ctx.getParam().getAgentKey())) {
            throw new AstroConfigException(ErrorCode.AGENT_KEY_REQUIRED,
                    "Please pass agentKey, or configure an AI_AGENT record with IS_DEFAULT=1 in the current environment");
        }
        if (StringUtils.isBlank(ctx.getParam().getMemoryKey())) {
            ctx.getParam().setMemoryKey(UUID.fastUUID().toString());
        }
    }
}
