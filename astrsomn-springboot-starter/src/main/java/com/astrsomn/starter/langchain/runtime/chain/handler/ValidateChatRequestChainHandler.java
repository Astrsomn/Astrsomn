package com.astrsomn.starter.langchain.runtime.chain.handler;

import cn.hutool.core.lang.UUID;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.starter.langchain.runtime.chain.AgentRuntimeChainHandler;
import com.astrsomn.starter.langchain.runtime.chain.AgentRuntimeContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 补齐会话级默认值，并校验 agentKey（在库表默认智能体补齐之后）。
 * <p>
 * 不在此校验 {@code userMessage}：{@code @Astro} 注入与 Assistant 缓存创建阶段通常尚无用户输入；
 * 发起对话时由业务入口（如 Controller、工作流节点）保证消息非空。
 */
@Component
@Order(20)
public class ValidateChatRequestChainHandler implements AgentRuntimeChainHandler {

    @Override
    public void handle(AgentRuntimeContext ctx) {
        if (StringUtils.isBlank(ctx.getParam().getAgentKey())) {
            throw new IllegalArgumentException(
                    "智能体 Key 不能为空：请传入 agentKey，或在当前环境下为 AI_AGENT 配置一条 IS_DEFAULT=1 的默认记录");
        }
        if (StringUtils.isBlank(ctx.getParam().getMemoryKey())) {
            ctx.getParam().setMemoryKey(UUID.fastUUID().toString());
        }
    }
}
