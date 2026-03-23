package org.astrsomn.starter.langchain;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import jakarta.annotation.Resource;
import opennlp.tools.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.astrsomn.core.common.entity.AiAgentEntity;
import org.astrsomn.core.common.langchain.AstrsomnChatAssistant;
import org.astrsomn.core.mapper.*;
import org.astrsomn.starter.langchain.factory.AiChatModelFactory;
import org.astrsomn.starter.langchain.factory.AiStreamModelFactory;
import org.astrsomn.starter.langchain.mcp.DynamicMcpToolProvider;
import org.astrsomn.starter.langchain.mcp.McpManager;
import org.astrsomn.starter.langchain.memory.DynamicMemoryProvider;
import org.astrsomn.starter.langchain.tool.CompositeToolProvider;
import org.astrsomn.starter.langchain.tool.DynamicToolProvider;
import org.springframework.context.ApplicationContext;
import org.astrsomn.core.common.langchain.buildParam.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private McpManager mcpManager;

    @Resource
    private AiMcpMapper aiMcpConfigMapper;

    @Resource
    private AiToolMapper aiToolMapper;

    @Resource
    private ApplicationContext applicationContext;


    @Resource
    private AiChatModelFactory aiChatModelFactory;


    @Resource
    private CompositeToolProvider compositeToolProvider;



    public <T> T createAssistant(AstroChatRequest<T> param) {
        // TODO 一、校验参数
        validateParam(param);

        // TODO 二、构建完整参数 | 优先级：代码配置 > 管理后台配置  >  默认配置
        buildParam(param);

        // TODO 三、构建业务接口和模型
        AiServices<T> builder = AiServices.builder(param.getServiceClass());
        if (param.getFeatures().isEnableStream()) {
            builder.streamingChatModel(aiStreamModelFactory.getStreamingModel(param));
        }else{
            builder.chatModel(aiChatModelFactory.getChatModel(param));
        }

        // TODO 四、组装Tool、Mcp、Rag
        configureComponents(builder, param);

        return builder.build();
    }

    private <T> void validateParam(AstroChatRequest<T> param) {
        if (StringUtils.isBlank(param.getAgentKey())) {
            throw new RuntimeException("智能体Key不能为空");
        }



    }



    private <T> void buildParam(AstroChatRequest<T> param) {

        AiAgentEntity aiAgentEntity = aiAgentMapper.selectOne(new LambdaUpdateWrapper<AiAgentEntity>()
                .eq(AiAgentEntity::getAgentKey, param.getAgentKey()));




    }


    private <T> void configureComponents(AiServices<T> builder, AstroChatRequest<T> param) {
        // 组装Memory
        if (param.getMaxHistoryMessages() > 0) {
            builder.chatMemoryProvider(memoryId-> {
                DynamicMemoryProvider memory = new DynamicMemoryProvider(aiConversationMapper);
                memory.initialize(param.getMaxHistoryMessages());
                return memory.get(param.getMemoryKey());
            });
        }
        ToolStrategy toolStrategy = param.getToolStrategy();
        // 组装Tool
        List<ToolProvider> providers = new ArrayList<>();

        if (toolStrategy.getMcpKeys() != null && !toolStrategy.getMcpKeys().isEmpty()) {
            DynamicMcpToolProvider mcpProvider = new DynamicMcpToolProvider(mcpManager, aiMcpConfigMapper);
            mcpProvider.initialize(toolStrategy.getMcpKeys());
            providers.add(mcpProvider);
        }

        if (toolStrategy.getToolKeys() != null && !toolStrategy.getToolKeys().isEmpty()) {
            DynamicToolProvider localProvider = new DynamicToolProvider(aiToolMapper, applicationContext);
            localProvider.initialize(toolStrategy.getToolKeys());
            providers.add(localProvider);
        }

        if (!providers.isEmpty()) {
            if (providers.size() == 1) {
                builder.toolProvider(providers.get(0));
            } else {
                builder.toolProvider(new CompositeToolProvider());
            }
        }

    }



}

