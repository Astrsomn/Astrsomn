package org.astrsomn.core.common.langchain;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface AstroChatAssistant {


    /**
     * 流式数据返回数据
     * @param userMessage   用户输入的消息
     * @param memoryKey 同一组对话的消息唯一值
     * @return
     */
    TokenStream stream(@UserMessage String userMessage, @MemoryId String memoryKey);


    /**
     * 返回全文数据
     * @param userMessage   用户输入的消息
     * @param memoryKey 同一组对话的消息唯一值
     * @return
     */
    String chat(@UserMessage String userMessage, @MemoryId String memoryKey);




}
