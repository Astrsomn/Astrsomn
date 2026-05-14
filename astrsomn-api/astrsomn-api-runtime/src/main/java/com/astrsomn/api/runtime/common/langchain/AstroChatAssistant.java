package com.astrsomn.api.runtime.common.langchain;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;

public interface AstroChatAssistant {

    
    TokenStream stream(@dev.langchain4j.service.UserMessage String userMessage, @MemoryId String memoryKey);

    
    TokenStream stream(@dev.langchain4j.service.UserMessage dev.langchain4j.data.message.UserMessage multiModalMessage, @MemoryId String memoryKey);

    
    String chat(@dev.langchain4j.service.UserMessage String userMessage, @MemoryId String memoryKey);

    
    String chat(@dev.langchain4j.service.UserMessage dev.langchain4j.data.message.UserMessage multiModalMessage, @MemoryId String memoryKey);

}