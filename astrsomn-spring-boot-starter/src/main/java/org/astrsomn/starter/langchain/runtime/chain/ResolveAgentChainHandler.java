package org.astrsomn.starter.langchain.runtime.chain;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.entity.AiAgentEntity;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.core.mapper.AiAgentMapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 加载 Agent 实体，并把 Agent 上的引用类字段合并进参数（不覆盖调用方已设值）。
 */
@Component
@Order(30)
@RequiredArgsConstructor
public class ResolveAgentChainHandler implements AgentRuntimeChainHandler {

    private final AiAgentMapper aiAgentMapper;

    @Override
    public void handle(AgentRuntimeContext ctx) {
        String agentKey = StringUtils.trimToNull(ctx.getParam().getAgentKey());
        AiAgentEntity agent = aiAgentMapper.selectOne(
                new LambdaQueryWrapper<AiAgentEntity>()
                        .eq(AiAgentEntity::getAgentKey, agentKey)
                        .eq(AiAgentEntity::getEnvCode, ctx.getEnvCode())
                        .eq(AiAgentEntity::getDeleted, false)
                        .last("LIMIT 1"));
        if (agent == null) {
            throw new IllegalStateException(
                    "未找到智能体配置: agentKey=" + agentKey + ", envCode=" + ctx.getEnvCode());
        }
        ctx.setAgent(agent);
        RuntimeChatParamMergeSupport.mergeAgentKeysIntoParam(ctx.getParam(), agent);
    }
}
