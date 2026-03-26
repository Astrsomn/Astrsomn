package org.astrsomn.starter.langchain;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.astrsomn.core.common.entity.AiAgentEntity;
import org.astrsomn.core.common.entity.AiMcpEntity;
import org.astrsomn.core.common.entity.AiToolEntity;
import org.astrsomn.core.common.langchain.buildParam.setting.ToolSetting;
import org.astrsomn.core.common.util.JsonUtil;
import org.astrsomn.core.mapper.*;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.langchain.cache.AssistantCacheManager;
import org.astrsomn.starter.langchain.factory.AiChatModelFactory;
import org.astrsomn.starter.langchain.factory.AiStreamModelFactory;
import org.astrsomn.starter.langchain.memory.ChatMemoryManager;
import org.astrsomn.starter.langchain.tool.local.LocalToolCacheManager;
import org.astrsomn.starter.langchain.tool.mcp.DynamicMcpToolProvider;
import org.astrsomn.starter.langchain.tool.mcp.McpToolCacheManager;
import org.astrsomn.starter.langchain.memory.DynamicMemoryProvider;

import org.astrsomn.starter.langchain.tool.local.DynamicToolProvider;
import org.astrsomn.starter.langchain.tool.UnionToolProvider;
import org.springframework.context.ApplicationContext;
import org.astrsomn.core.common.langchain.buildParam.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AstroAssistantFactory {

    private final AiStreamModelFactory aiStreamModelFactory;
    private final AiPromptMapper aiPromptMapper;
    private final AiAgentMapper aiAgentMapper;
    private final AiConversationMapper aiConversationMapper;
    private final McpToolCacheManager mcpToolManager;
    private final AiMcpMapper aiMcpMapper;
    private final AiToolMapper aiToolMapper;
    private final ApplicationContext applicationContext;
    private final AiChatModelFactory aiChatModelFactory;
    private final LocalToolCacheManager globalToolCache;
    private final AstrsomnProperties astrsomnProperties;
    private final ChatMemoryManager chatMemoryManager;
    private final AssistantCacheManager cacheManager;


    public <T> T createAssistant(AstroChatParam<T> param) {

        buildParam(param);

        return cacheManager.getOrCreate(param, () -> {
            AiServices<T> builder = AiServices.builder(param.getServiceClass());
            if (param.getChatSetting().isEnableStream()) {
                builder.streamingChatModel(aiStreamModelFactory.getStreamingModel(param));
            } else {
                builder.chatModel(aiChatModelFactory.getChatModel(param));
            }
            configureComponents(builder, param);
            return builder.build();
        });

    }


    private <T> void buildParam(AstroChatParam<T> param) {
        if (StringUtils.isBlank(param.getUserMessage())) {
            throw new RuntimeException("用户消息不能为空");
        }
        if (StringUtils.isBlank(param.getAgentKey())) {
            throw new RuntimeException("智能体Key不能为空");
        }
        if (StringUtils.isBlank(param.getMemoryKey())) {
            param.setMemoryKey(UUID.fastUUID().toString());
        }
        AiAgentEntity aiAgentEntity = aiAgentMapper.selectOne(new LambdaUpdateWrapper<AiAgentEntity>()
                .eq(AiAgentEntity::getAgentKey, param.getAgentKey())
                .eq(AiAgentEntity::getEnvCode, astrsomnProperties.getEnvCode()));

        param.getModelSetting()
                .setSeed(aiAgentEntity.getSeed())
                .setTopK(aiAgentEntity.getTopK())
                .setTopP(aiAgentEntity.getTopP())
                .setFrequencyPenalty(aiAgentEntity.getFrequencyPenalty())
                .setPresencePenalty(aiAgentEntity.getPresencePenalty())
                .setMaxTokens(aiAgentEntity.getMaxTokens())
                .setTemperature(aiAgentEntity.getTemperature());


        param.getToolSetting()
                .setToolKeys(JsonUtil.parseArray(aiAgentEntity.getToolKeys(), String.class))
                .setMcpKeys(JsonUtil.parseArray(aiAgentEntity.getMcpKeys(), String.class));

    }


    private <T> void configureComponents(AiServices<T> builder, AstroChatParam<T> param) {

        if (param.getMaxHistoryMessages() > 0) {
            builder.chatMemoryProvider(new DynamicMemoryProvider(chatMemoryManager, param.getMaxHistoryMessages()));
        }

        if (Objects.nonNull(param.getToolSetting())) {
            ToolSetting toolSetting = param.getToolSetting();
            List<ToolProvider> providers = new ArrayList<>();
            if (toolSetting.getMcpKeys() != null && !toolSetting.getMcpKeys().isEmpty()) {
                List<AiMcpEntity> mcpConfigs = aiMcpMapper.selectList(
                        new LambdaQueryWrapper<AiMcpEntity>()
                                .in(AiMcpEntity::getMcpKey, toolSetting.getMcpKeys())
                                .eq(AiMcpEntity::getEnvCode, astrsomnProperties.getEnvCode()));
                providers.add(new DynamicMcpToolProvider(mcpConfigs, mcpToolManager));

            }
            if (toolSetting.getToolKeys() != null && !toolSetting.getToolKeys().isEmpty()) {
                List<AiToolEntity> toolConfigs = aiToolMapper.selectList(
                        new LambdaQueryWrapper<AiToolEntity>()
                                .in(AiToolEntity::getToolKey, toolSetting.getToolKeys())
                                .eq(AiToolEntity::getEnvCode, astrsomnProperties.getEnvCode()));
                providers.add(new DynamicToolProvider(toolConfigs, applicationContext, globalToolCache));
            }

            if (CollectionUtil.isNotEmpty(providers)) {
                builder.toolProvider(new UnionToolProvider(providers));
            }
        }


    }


}

