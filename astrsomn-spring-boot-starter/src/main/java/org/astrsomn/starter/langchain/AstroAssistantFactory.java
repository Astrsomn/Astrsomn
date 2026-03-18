package org.astrsomn.starter.langchain;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.astrsomn.core.common.langchain.AstrsomnChatAssistant;
import org.astrsomn.core.mapper.AiMcpMapper;
import org.astrsomn.core.mapper.AiPromptMapper;
import org.astrsomn.core.mapper.AiToolMapper;
import org.astrsomn.starter.langchain.factory.AiChatModelFactory;
import org.astrsomn.starter.langchain.factory.AiStreamModelFactory;
import org.astrsomn.starter.langchain.mcp.DynamicMcpToolProvider;
import org.astrsomn.starter.langchain.mcp.McpManager;
import org.astrsomn.starter.langchain.memory.DynamicMemoryProvider;
import org.astrsomn.starter.langchain.tool.DynamicToolProvider;
import org.springframework.context.ApplicationContext;
import org.astrsomn.core.common.langchain.buildParam.*;
import java.util.Optional;

public class AstroAssistantFactory {
    @Resource
    private AiStreamModelFactory aiStreamModelFactory;
//
    @Resource
    private AiPromptMapper aiPromptMapper;
//
//    @Resource
//    private AiConversationFacade aiConversationFacade;

    @Resource
    private McpManager mcpManager;

    @Resource
    private AiMcpMapper aiMcpConfigMapper;

    @Resource
    private AiToolMapper aiToolMapper;

    @Resource
    private ApplicationContext applicationContext;
//
//    @Resource
//    private QdrantUtil qdrantUtil;

    @Resource
    private EmbeddingModelRegistry modelRegistry;

    @Resource
    private AiChatModelFactory aiChatModelFactory;

    // TODO 构建AI处理模型
    public AstrsomnChatAssistant buildChatAssistant(AiChatBuildParam buildParam) {

        AiServices<AstrsomnChatAssistant> builder = AiServices.builder(buildParam.getClazz())
                .systemMessageProvider(tempMemory -> aiPromptMapper.getByUUID(buildParam.getPromptUuid()));
        if (buildParam.isEnableStream()) {
            builder.streamingChatModel(aiStreamModelFactory.getStreamingLanguageModel(buildParam));
        } else {
            builder.chatModel(aiChatModelFactory.getLanguageModel(buildParam));
        }

        // TODO 1. 优雅地配置 Memory
        Optional.ofNullable(buildParam.getMaxMessages())
                .map(max -> {
                    DynamicMemoryProvider memory = new DynamicMemoryProvider(aiConversationFacade);
                    memory.initialize(max);
                    return memory;
                })
                .ifPresent(builder::chatMemoryProvider);


        // TODO 2. 优雅地配置 知识库
//        Optional.ofNullable(buildParam.getRagIdList())
//                .filter(list -> !list.isEmpty())
//                .ifPresent(ids -> {
//                    EmbeddingModel matchedModel = modelRegistry.getModelByDimension(buildParam.getVectorSize());
//                    DynamicRagProvider provider = new DynamicRagProvider(qdrantUtil, matchedModel);
//                    provider.initialize(ids);
//                    builder.contentRetriever(provider);
//                });

        // TODO 3. 优雅地配置 MCP Tools
        Optional.ofNullable(buildParam.getMcpIdList())
                .filter(list -> !list.isEmpty())
                .ifPresent(ids -> {
                    DynamicMcpToolProvider provider = new DynamicMcpToolProvider(mcpManager, aiMcpConfigMapper);
                    provider.initialize(ids);
                    builder.toolProvider(provider);
                });

        // TODO 4. 优雅地配置 本地 Tools
        Optional.ofNullable(buildParam.getToolIdList())
                .filter(list -> !list.isEmpty())
                .ifPresent(ids -> {
                    DynamicToolProvider provider = new DynamicToolProvider(aiToolMapper, applicationContext);
                    provider.initialize(ids);
                    builder.toolProvider(provider);
                });

        return builder.build();
    }
}

