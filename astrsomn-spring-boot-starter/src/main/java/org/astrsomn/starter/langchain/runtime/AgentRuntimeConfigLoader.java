package org.astrsomn.starter.langchain.runtime;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.astrsomn.core.common.entity.AiAgentEntity;
import org.astrsomn.core.common.entity.AiInstanceEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ChatSetting;
import org.astrsomn.core.common.langchain.buildParam.setting.ToolSetting;
import org.astrsomn.core.common.util.JsonUtil;
import org.astrsomn.core.mapper.AiAgentMapper;
import org.astrsomn.core.mapper.AiInstanceMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.springframework.stereotype.Component;

/**
 * 从数据库加载 Agent 配置并合并进 {@link AstroChatParam}（模型采样参数、工具/MCP 引用等）。
 * 合并规则：调用方在 {@link AstroChatParam} 中已设置的项优先，其余用库中 Agent 记录补齐。
 */
@Component
@RequiredArgsConstructor
public class AgentRuntimeConfigLoader {

    private final AiAgentMapper aiAgentMapper;
    private final AstrsomnProperties astrsomnProperties;
    private final AiInstanceMapper aiInstanceMapper;

    public <T> void validateAndApplyAgent(AstroChatParam<T> param) {
        if (StringUtils.isBlank(param.getUserMessage())) {
            throw new RuntimeException("用户消息不能为空");
        }
        if (StringUtils.isBlank(param.getAgentKey())) {
            throw new RuntimeException("智能体Key不能为空");
        }
        if (StringUtils.isBlank(param.getMemoryKey())) {
            param.setMemoryKey(UUID.fastUUID().toString());
        }

        AiAgentEntity agent = aiAgentMapper.selectOne(new LambdaQueryWrapper<AiAgentEntity>()
                .eq(AiAgentEntity::getAgentKey, param.getAgentKey())
                .eq(AiAgentEntity::getEnvCode, astrsomnProperties.getEnvCode()));
        if (agent == null) {
            throw new RuntimeException("未找到智能体: agentKey=" + param.getAgentKey()
                    + ", envCode=" + astrsomnProperties.getEnvCode());
        }

        String instanceKey = StringUtils.firstNonBlank(param.getInstanceKey(), agent.getChatInstanceKey());
        param.setInstanceKey(instanceKey);
        AiInstanceEntity aiInstance = null;
        if (StringUtils.isNotBlank(instanceKey)) {
            aiInstance = aiInstanceMapper.selectOne(new LambdaQueryWrapper<AiInstanceEntity>()
                    .eq(AiInstanceEntity::getInstanceKey, instanceKey)
                    .eq(AiInstanceEntity::getEnvCode, agent.getEnvCode()));
        }
        if (aiInstance != null && StringUtils.isNotBlank(aiInstance.getModelKey())) {
            param.setModelKey(aiInstance.getModelKey());
        }
        if (StringUtils.isBlank(param.getModelKey())) {
            throw new RuntimeException("智能体未配置对话实例 (chatInstanceKey) 或实例未关联模型");
        }

        mergeModelFromInstance(ensureModelSetting(param), aiInstance);
        mergeToolSettingsFromAgent(ensureToolSetting(param), agent);
    }

    private static <T> ChatSetting ensureModelSetting(AstroChatParam<T> param) {
        if (param.getChatSetting() == null) {
            param.setChatSetting(new ChatSetting());
        }
        return param.getChatSetting();
    }

    private static <T> ToolSetting ensureToolSetting(AstroChatParam<T> param) {
        if (param.getToolSetting() == null) {
            param.setToolSetting(new ToolSetting());
        }
        return param.getToolSetting();
    }

    /**
     * 仅填充用户未在 {@link ChatSetting} 中赋值的字段（null 视为未指定，由实例表补齐）。
     */
    private void mergeModelFromInstance(ChatSetting target, AiInstanceEntity instance) {
        if (instance == null) {
            return;
        }
        if (target.getTemperature() == null) {
            target.setTemperature(instance.getTemperature());
        }
        if (target.getTopP() == null) {
            target.setTopP(instance.getTopP());
        }
        if (target.getTopK() == null) {
            target.setTopK(instance.getTopK());
        }
        if (target.getMaxTokens() == null) {
            target.setMaxTokens(instance.getMaxTokens());
        }
        if (target.getSeed() == null) {
            target.setSeed(instance.getSeed());
        }
        if (target.getPresencePenalty() == null) {
            target.setPresencePenalty(instance.getPresencePenalty());
        }
        if (target.getFrequencyPenalty() == null) {
            target.setFrequencyPenalty(instance.getFrequencyPenalty());
        }
    }

    /**
     * 仅当用户未显式提供 toolKeys / mcpKeys（为 null）时从 Agent 记录解析；非 null（含空列表）均以用户为准。
     */
    private void mergeToolSettingsFromAgent(ToolSetting target, AiAgentEntity agent) {
        if (target.getToolKeys() == null) {
            target.setToolKeys(JsonUtil.parseArray(agent.getToolKeys(), String.class));
        }
        if (target.getMcpKeys() == null) {
            target.setMcpKeys(JsonUtil.parseArray(agent.getMcpKeys(), String.class));
        }
    }
}
