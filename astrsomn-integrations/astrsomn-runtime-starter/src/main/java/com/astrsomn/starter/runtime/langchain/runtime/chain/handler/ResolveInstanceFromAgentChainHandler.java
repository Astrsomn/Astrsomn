package com.astrsomn.starter.runtime.langchain.runtime.chain.handler;

import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.langchain.exception.InstanceNotFoundException;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeChainHandler;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeContext;
import com.astrsomn.starter.runtime.mapper.AstAiInstanceMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
@Component
@Order(35)
@RequiredArgsConstructor
public class ResolveInstanceFromAgentChainHandler implements AgentRuntimeChainHandler {

    private static final String IS_DEFAULT_YES = "Y";

    private final AstAiInstanceMapper aiInstanceMapper;

    @Override
    public void handle(AgentRuntimeContext ctx) {
        // 调用方已显式指定 instanceKey，跳过自动选择
        if (StringUtils.isNotBlank(ctx.getParam().getInstanceKey())) {
            return;
        }

        AiAgentEntity agent = ctx.getAgent();
        if (agent == null) {
            return;
        }

        String agentKey = StringUtils.trimToNull(agent.getAgentKey());
        if (agentKey == null) {
            return;
        }

        List<AiInstanceEntity> instances = aiInstanceMapper.selectList(
                new LambdaQueryWrapper<AiInstanceEntity>()
                        .eq(AiInstanceEntity::getBizKey, agentKey)
                        .eq(AiInstanceEntity::getEnvCode, ctx.getEnvCode())
                        .eq(AiInstanceEntity::getStatus, "enabled")
                        .eq(AiInstanceEntity::getDeleted, false)
                        .orderByAsc(AiInstanceEntity::getCreateTime));

        if (instances == null || instances.isEmpty()) {
            throw new InstanceNotFoundException(agentKey, ctx.getEnvCode(), true);
        }

        AiInstanceEntity selected = selectInstance(instances, agent.getRouteStrategy(), ctx);
        ctx.getParam().setInstanceKey(selected.getInstanceKey());
        log.debug("Agent [{}] 自动选择实例: {} (策略: {})", agentKey, selected.getInstanceKey(), agent.getRouteStrategy());
    }

    private AiInstanceEntity selectInstance(List<AiInstanceEntity> instances, String routeStrategy, AgentRuntimeContext ctx) {
        if (instances.size() == 1) {
            return instances.get(0);
        }

        // 优先选择标记为默认的实例
        for (AiInstanceEntity inst : instances) {
            if (IS_DEFAULT_YES.equals(inst.getIsDefault())) {
                return inst;
            }
        }

        String strategy = StringUtils.trimToNull(routeStrategy);
        if (strategy == null) {
            strategy = "weightedRandom";
        }

        return switch (strategy) {
            case "roundRobin" -> selectRoundRobin(instances, ctx);
            case "random" -> selectRandom(instances);
            default -> selectWeightedRandom(instances);
        };
    }

    private AiInstanceEntity selectWeightedRandom(List<AiInstanceEntity> instances) {
        int totalWeight = 0;
        for (AiInstanceEntity inst : instances) {
            int w = inst.getRouteWeight() != null && inst.getRouteWeight() > 0 ? inst.getRouteWeight() : 1;
            totalWeight += w;
        }
        if (totalWeight <= 0) {
            return instances.get(0);
        }
        int r = ThreadLocalRandom.current().nextInt(totalWeight);
        for (AiInstanceEntity inst : instances) {
            int w = inst.getRouteWeight() != null && inst.getRouteWeight() > 0 ? inst.getRouteWeight() : 1;
            r -= w;
            if (r < 0) {
                return inst;
            }
        }
        return instances.get(0);
    }

    private AiInstanceEntity selectRoundRobin(List<AiInstanceEntity> instances, AgentRuntimeContext ctx) {
        // 使用 agentKey 的 hashCode 作为轮询起点，保证同一 agent 的不同请求有分布
        int idx = Math.floorMod(
                System.identityHashCode(ctx.getParam()) + ThreadLocalRandom.current().nextInt(instances.size()),
                instances.size());
        return instances.get(idx);
    }

    private AiInstanceEntity selectRandom(List<AiInstanceEntity> instances) {
        return instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
    }
}
