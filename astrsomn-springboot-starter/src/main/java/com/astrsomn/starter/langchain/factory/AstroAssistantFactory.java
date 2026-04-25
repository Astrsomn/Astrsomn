package com.astrsomn.starter.langchain.factory;


import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.core.common.langchain.buildParam.setting.RagSetting;
import com.astrsomn.starter.langchain.cache.AssistantCacheManager;
import com.astrsomn.starter.langchain.memory.ChatMemoryManager;
import com.astrsomn.starter.langchain.memory.DynamicMemoryProvider;
import com.astrsomn.starter.langchain.quota.AstroModelListener;
import com.astrsomn.starter.langchain.runtime.AgentRuntimeConfigLoader;
import com.astrsomn.starter.langchain.tool.ToolProviderAssembler;
import com.astrsomn.starter.langchain.tool.rag.DynamicRagProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AstroAssistantFactory {

    private final ChatMemoryManager chatMemoryManager;
    private final AssistantCacheManager cacheManager;
    private final AgentRuntimeConfigLoader agentRuntimeConfigLoader;
    private final ToolProviderAssembler toolProviderAssembler;
    private final DynamicRagProvider ragComponentAssembler;
    private final AstroModelFactory astroModelFactory;
    private final AstroModelListener  astroModelListener;

    public <T> T createAssistant(AstroChatParam<T> param) {
        agentRuntimeConfigLoader.validateAndApplyAgent(param);
        param.setChatModelListeners(List.of(astroModelListener.createBindingListener(param)));

        AiServices<T> builder = AiServices.builder(param.getServiceClass());

        boolean isStream = param.getConversationSetting().isEnableStream();

        Optional.of(isStream)
                .filter(Boolean::booleanValue)
                .map(b -> astroModelFactory.createModel(param, StreamingChatModel.class))
                .ifPresent(builder::streamingChatModel);

        Optional.of(isStream)
                .filter(b -> !b)
                .map(b -> astroModelFactory.createModel(param, ChatModel.class))
                .ifPresent(builder::chatModel);

        configureComponents(builder, param);
        return builder.build();
    }

    private <T> void configureComponents(AiServices<T> builder, AstroChatParam<T> param) {
        // TODO 组装调用工具
        Optional.ofNullable(toolProviderAssembler.assemble(param))
                .ifPresent(builder::toolProvider);

        // TODO 组装历史消息
        Optional.ofNullable(param.getMaxHistoryMessages())
                .filter(max -> max > 0)
                .map(max -> new DynamicMemoryProvider(chatMemoryManager, max))
                .ifPresent(builder::chatMemoryProvider);

        // TODO 组装Rag
        Optional.ofNullable(param.getRagSetting())
                .filter(RagSetting::isEnabled)
                .map(setting -> ragComponentAssembler.createRetriever(param))
                .ifPresent(builder::contentRetriever);
    }
}
