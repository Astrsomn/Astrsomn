package org.astrsomn.starter.langchain;


import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import jakarta.annotation.Resource;
import org.astrsomn.core.common.langchain.AstrsomnChatAssistant;
import org.astrsomn.core.mapper.AiConversationMapper;
import org.astrsomn.core.mapper.AiMcpMapper;
import org.astrsomn.core.mapper.AiPromptMapper;
import org.astrsomn.core.mapper.AiToolMapper;
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
        // 1.校验参数合法性
        validateParam(param);

        // 2.确保类型绝对一直
        AiServices<T> builder = AiServices.builder(param.getServiceClass());

        // 3.组装模型
        if (param.getFeatures().isEnableStream()) {
            builder.streamingChatModel(aiStreamModelFactory.getStreamingModel(param));
        }else{
            builder.chatModel(aiChatModelFactory.getChatModel(param));
        }

        //4.组装组件
        configureComponents(builder, param);


        return builder.build();
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

    private <T> void validateParam(AstroChatRequest<T> param) {




    }


}

