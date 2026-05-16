package com.astrsomn.starter.runtime.langchain.runtime.chain.handler;

import cn.hutool.core.lang.UUID;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.langchain.exception.AstroConfigException;
import com.astrsomn.starter.runtime.langchain.exception.ErrorCode;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeChainHandler;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 补齐会话级默认值，并校验 agentKey（在库表默认智能体补齐之后）。
 * <p>
 * 不在此校验用户消息文本：{@code @Astro} 注入与 Assistant 缓存创建阶段通常尚无用户输入；
 * 发起对话时由业务入口（如 Controller、工作流节点）保证消息非空。
 */
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
