package com.astrsomn.server.astrsomn.assistant;

import com.astrsomn.api.runtime.common.langchain.AstroChatAssistant;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface PromptAssistant extends AstroChatAssistant {


    @SystemMessage("请你美化一下这段提示词，请直接给出完整的答案，不需要复述问题")
    String submit(@UserMessage String userMessage, @MemoryId String memoryKey);


}
