package com.astrsomn.api.runtime.common.langchain;

import com.astrsomn.api.runtime.common.langchain.buildParam.setting.*;
import lombok.Data;

@Data
public class AstroBuilderChatRequest {

    private String userMessage;

    private String memoryKey;

    private ModelSetting modelSetting;

    private ChatSetting chatSetting;

    private PromptSetting promptSetting;

    private ConversationSetting conversationSetting;

    private ToolSetting toolSetting;

    private RagSetting ragSetting;

    private Integer maxHistoryMessages;

    private boolean enableHistorySave;
}
