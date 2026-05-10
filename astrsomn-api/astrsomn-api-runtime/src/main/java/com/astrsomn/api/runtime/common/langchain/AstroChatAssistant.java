package com.astrsomn.api.runtime.common.langchain;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;

public interface AstroChatAssistant {

    /**
     * 【文本流式】
     */
    TokenStream stream(@dev.langchain4j.service.UserMessage String userMessage, @MemoryId String memoryKey);

    /**
     * 【多模态流式】
     * 修复方案：给 UserMessage 对象也加上 @UserMessage 注解
     */
    TokenStream stream(@dev.langchain4j.service.UserMessage dev.langchain4j.data.message.UserMessage multiModalMessage, @MemoryId String memoryKey);

    /**
     * 【文本同步】
     */
    String chat(@dev.langchain4j.service.UserMessage String userMessage, @MemoryId String memoryKey);

    /**
     * 【多模态同步】
     * 修复方案：同上
     */
    String chat(@dev.langchain4j.service.UserMessage dev.langchain4j.data.message.UserMessage multiModalMessage, @MemoryId String memoryKey);

}
