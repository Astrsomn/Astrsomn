package org.astrsomn.starter.langchain;


import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.astrsomn.starter.langchain.cache.AssistantCacheManager;
import org.astrsomn.starter.langchain.factory.AiChatModelFactory;
import org.astrsomn.starter.langchain.factory.AiStreamModelFactory;
import org.astrsomn.starter.langchain.memory.ChatMemoryManager;
import org.astrsomn.starter.langchain.memory.DynamicMemoryProvider;
import org.astrsomn.starter.langchain.runtime.AgentRuntimeConfigLoader;
import org.astrsomn.starter.langchain.runtime.ToolProviderAssembler;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.starter.langchain.tool.rag.RagComponentAssembler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AstroAssistantFactory {

    private final AiStreamModelFactory aiStreamModelFactory;
    private final AiChatModelFactory aiChatModelFactory;
    private final ChatMemoryManager chatMemoryManager;
    private final AssistantCacheManager cacheManager;
    private final AgentRuntimeConfigLoader agentRuntimeConfigLoader;
    private final ToolProviderAssembler toolProviderAssembler;
    private final RagComponentAssembler ragComponentAssembler;


    public <T> T createAssistant(AstroChatParam<T> param) {
        agentRuntimeConfigLoader.validateAndApplyAgent(param);

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

    private <T> void configureComponents(AiServices<T> builder, AstroChatParam<T> param) {
        if (param.getMaxHistoryMessages() > 0) {
            builder.chatMemoryProvider(new DynamicMemoryProvider(chatMemoryManager, param.getMaxHistoryMessages()));
        }
        toolProviderAssembler.assemble(param).ifPresent(builder::toolProvider);

        if (param.getRagSetting() != null && param.getRagSetting().isEnabled()) {
            ContentRetriever retriever = ragComponentAssembler.createRetriever(param);
            builder.contentRetriever(retriever);
        }
    }
}
