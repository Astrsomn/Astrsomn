package com.astrsomn.api.runtime.common.langchain.buildParam;


import com.astrsomn.api.runtime.common.langchain.buildParam.setting.*;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@Accessors(chain = true)
@Builder
public class AstroChatParam<T> {

    // --- 1. 核心会话上下文 ---


    private final Class<T> serviceClass;
    private String userMessageText;
    private List<String> fileUrlList;
    private UserMessage userMessage;
    private String bizKey;
    private String modelKey;
    private String instanceKey;
    private String memoryKey;

    // --- 2. 组合策略对象 (核心改动) ---
    @Builder.Default
    private Integer maxHistoryMessages = 10;
    @Builder.Default
    private ChatSetting chatSetting = new ChatSetting();
    @Builder.Default
    private ConversationSetting conversationSetting = new ConversationSetting();
    @Builder.Default
    private ToolSetting toolSetting = new ToolSetting();
    @Builder.Default
    private PromptSetting promptSetting = new PromptSetting();
    @Builder.Default
    private RagSetting ragSetting = new RagSetting();
    @Builder.Default
    private ImageSetting imageSetting = new ImageSetting();
    @Builder.Default
    private EmbeddingSetting embeddingSetting = new EmbeddingSetting();
    @Builder.Default
    private ModelSetting modelSetting = new ModelSetting();
    private List<ChatModelListener> chatModelListeners;
    @Builder.Default
    private boolean enableHistorySave = true;
    @Builder.Default
    private transient AtomicBoolean streamTurnPersisted = new AtomicBoolean(false);

    public static <T> AstroChatParam<T> of(Class<T> serviceClass, String bizKey) {
        return AstroChatParam.<T>builder()
                .serviceClass(serviceClass)
                .bizKey(bizKey)
                .build();
    }

    public void markStreamTurnPersisted() {
        streamTurnPersisted.set(true);
    }

    public boolean isStreamTurnPersisted() {
        return streamTurnPersisted.get();
    }


}