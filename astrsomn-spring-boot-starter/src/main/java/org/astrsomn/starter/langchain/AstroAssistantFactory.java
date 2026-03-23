package org.astrsomn.starter.langchain;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.astrsomn.core.common.entity.AiAgentEntity;
import org.astrsomn.core.common.entity.AiMcpEntity;
import org.astrsomn.core.common.entity.AiToolEntity;
import org.astrsomn.core.common.langchain.buildParam.setting.ToolSetting;
import org.astrsomn.core.common.util.JsonUtil;
import org.astrsomn.core.mapper.*;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.langchain.factory.AiChatModelFactory;
import org.astrsomn.starter.langchain.factory.AiStreamModelFactory;
import org.astrsomn.starter.langchain.memory.ChatMemoryManager;
import org.astrsomn.starter.langchain.tool.local.LocalToolCacheManager;
import org.astrsomn.starter.langchain.tool.mcp.DynamicMcpToolProvider;
import org.astrsomn.starter.langchain.tool.mcp.McpToolCacheManager;
import org.astrsomn.starter.langchain.memory.DynamicMemoryProvider;

import org.astrsomn.starter.langchain.tool.local.DynamicToolProvider;
import org.astrsomn.starter.langchain.tool.UnionToolProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.astrsomn.core.common.langchain.buildParam.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AstroAssistantFactory {

    @Resource
    private AiStreamModelFactory aiStreamModelFactory;

    @Resource
    private AiPromptMapper aiPromptMapper;

    @Resource
    private AiAgentMapper aiAgentMapper;
    @Resource
    private AiConversationMapper aiConversationMapper;

    @Resource
    private McpToolCacheManager mcpToolManager;

    @Resource
    private AiMcpMapper aiMcpMapper;

    @Resource
    private AiToolMapper aiToolMapper;

    @Resource
    private ApplicationContext applicationContext;


    @Resource
    private AiChatModelFactory aiChatModelFactory;

    @Resource
    private LocalToolCacheManager globalToolCache;

    @Resource
    private AstrsomnProperties astrsomnProperties;

    @Autowired
    private ChatMemoryManager chatMemoryManager;

    public <T> T createAssistant(AstroChatRequest<T> param) {

        // TODO 一、构建完整参数 | 优先级：代码配置 > 管理后台配置  >  默认配置
        buildParam(param);

        // TODO 二、构建业务接口和模型
        AiServices<T> builder = AiServices.builder(param.getServiceClass());
        if (param.getChatSetting().isEnableStream()) {
            builder.streamingChatModel(aiStreamModelFactory.getStreamingModel(param));
        } else {
            builder.chatModel(aiChatModelFactory.getChatModel(param));
        }

        // TODO 三、组装Tool、Mcp、Rag
        configureComponents(builder, param);

        return builder.build();
    }


    private <T> void buildParam(AstroChatRequest<T> param) {
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


    private <T> void configureComponents(AiServices<T> builder, AstroChatRequest<T> param) {

        if (param.getMaxHistoryMessages() > 0) {
            // 传入 Manager 和本次请求指定的参数
            builder.chatMemoryProvider(new DynamicMemoryProvider(chatMemoryManager, param.getMaxHistoryMessages()));
        }



        ToolSetting toolSetting = param.getToolSetting();
        List<ToolProvider> providers = new ArrayList<>();
        if (toolSetting.getMcpKeys() != null && !toolSetting.getMcpKeys().isEmpty()) {
            List<AiMcpEntity> mcpConfigs = aiMcpMapper.selectList(new LambdaQueryWrapper<AiMcpEntity>()
                    .in(AiMcpEntity::getMcpKey, toolSetting.getMcpKeys()));
            providers.add(new DynamicMcpToolProvider(mcpConfigs, mcpToolManager));

        }
        if (toolSetting.getToolKeys() != null && !toolSetting.getToolKeys().isEmpty()) {
            List<AiToolEntity> configs = aiToolMapper.selectList(new LambdaQueryWrapper<AiToolEntity>()
                    .in(AiToolEntity::getToolKey, toolSetting.getToolKeys()));
            DynamicToolProvider localProvider = new DynamicToolProvider(configs, applicationContext, globalToolCache);
            providers.add(localProvider);

        }

        if (CollectionUtil.isNotEmpty(providers)) {
            builder.toolProvider(new UnionToolProvider(providers));
        }

    }


}

