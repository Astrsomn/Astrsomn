package com.astrsomn.starter.runtime.langchain.runtime.chain.handler;

import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.langchain.exception.InstanceNotFoundException;
import com.astrsomn.starter.runtime.langchain.runtime.AiRuntimeDefaultsResolver;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeChainHandler;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeContext;
import com.astrsomn.starter.runtime.langchain.runtime.chain.RuntimeChatParamMergeSupport;
import com.astrsomn.starter.runtime.mapper.AstAiInstanceMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@Order(40)
@RequiredArgsConstructor
public class ResolveInstanceChainHandler implements AgentRuntimeChainHandler {

    private static final String IS_DEFAULT_YES = "Y";
    private static final String STRATEGY_WEIGHTED_RANDOM = "weightedRandom";
    private static final String STRATEGY_ROUND_ROBIN = "roundRobin";
    private static final String STRATEGY_RANDOM = "random";

    private final AstAiInstanceMapper aiInstanceMapper;
    private final AiRuntimeDefaultsResolver aiRuntimeDefaultsResolver;

    @Override
    public void handle(AgentRuntimeContext ctx) {
        String instanceKey = StringUtils.trimToNull(ctx.getParam().getInstanceKey());
        AiInstanceEntity instance = null;

        // 1. 指定了 instanceKey（用户传入 或 ResolveInstanceFromAgentChainHandler 自动选择）
        //    精确匹配，不走路由，找不到直接报错
        if (instanceKey != null) {
            instance = aiInstanceMapper.selectOne(
                    new LambdaQueryWrapper<AiInstanceEntity>()
                            .eq(AiInstanceEntity::getInstanceKey, instanceKey)
                            .eq(AiInstanceEntity::getEnvCode, ctx.getEnvCode())
                            .eq(AiInstanceEntity::getDeleted, false)
                            .last("LIMIT 1"));
            if (instance == null) {
                throw new InstanceNotFoundException(instanceKey, ctx.getEnvCode());
            }
        }

        // 2. 没有指定 instance，从 agent 的所有实例中按路由策略选择（降级/转移）
        if (instance == null) {
            AiAgentEntity agent = ctx.getAgent();
            if (agent != null) {
                String agentKey = StringUtils.trimToNull(agent.getAgentKey());
                if (agentKey != null) {
                    List<AiInstanceEntity> instances = aiInstanceMapper.selectList(
                            new LambdaQueryWrapper<AiInstanceEntity>()
                                    .eq(AiInstanceEntity::getBizKey, agentKey)
                                    .eq(AiInstanceEntity::getEnvCode, ctx.getEnvCode())
                                    .eq(AiInstanceEntity::getStatus, "enabled")
                                    .eq(AiInstanceEntity::getDeleted, false)
                                    .orderByAsc(AiInstanceEntity::getCreateTime));

                    if (instances != null && !instances.isEmpty()) {
                        String strategy = StringUtils.trimToNull(agent.getRouteStrategy());
                        instance = selectInstance(instances, strategy);
                        log.debug("Agent [{}] 路由选择实例: {} (策略: {})",
                                agentKey, instance.getInstanceKey(), strategy);
                    } else {
                        throw new InstanceNotFoundException(agentKey, ctx.getEnvCode(), true);
                    }
                }
            }
        }

        // 3. 仍未找到 → 报错
        if (instance == null) {
            throw new InstanceNotFoundException(
                    StringUtils.trimToNull(ctx.getParam().getInstanceKey()), ctx.getEnvCode());
        }

        ctx.setInstance(instance);
        RuntimeChatParamMergeSupport.mergeChatSettingFromInstance(ctx.getParam().getChatSetting(), instance);
        if (StringUtils.isNotBlank(instance.getModelRouteJson())) {
            RuntimeChatParamMergeSupport.mergeModelRouteFromJson(ctx.getParam().getModelSetting(), instance.getModelRouteJson());
        }
        if (StringUtils.isBlank(ctx.getParam().getModelKey())) {
            ctx.getParam().setModelKey(StringUtils.trimToNull(instance.getModelKey()));
        }
        if (StringUtils.isBlank(ctx.getParam().getModelKey())) {
            aiRuntimeDefaultsResolver
                    .resolveDefaultChatModelKey(ctx.getEnvCode())
                    .ifPresent(ctx.getParam()::setModelKey);
        }
    }

    private AiInstanceEntity selectInstance(List<AiInstanceEntity> instances, String routeStrategy) {
        if (instances.size() == 1) {
            return instances.get(0);
        }

        // 优先选择标记为默认的实例
        for (AiInstanceEntity inst : instances) {
            if (IS_DEFAULT_YES.equals(inst.getIsDefault())) {
                return inst;
            }
        }

        String strategy = routeStrategy != null ? routeStrategy : STRATEGY_WEIGHTED_RANDOM;

        return switch (strategy) {
            case STRATEGY_ROUND_ROBIN -> selectRoundRobin(instances);
            case STRATEGY_RANDOM -> selectRandom(instances);
            default -> selectWeightedRandom(instances);
        };
    }

    private AiInstanceEntity selectWeightedRandom(List<AiInstanceEntity> instances) {
        int totalWeight = 0;
        for (AiInstanceEntity inst : instances) {
            int w = inst.getRouteWeight() != null && inst.getRouteWeight() > 0 ? inst.getRouteWeight() : 1;
            totalWeight += w;
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

    private AiInstanceEntity selectRoundRobin(List<AiInstanceEntity> instances) {
        int idx = ThreadLocalRandom.current().nextInt(instances.size());
        return instances.get(idx);
    }

    private AiInstanceEntity selectRandom(List<AiInstanceEntity> instances) {
        return instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
    }
}
