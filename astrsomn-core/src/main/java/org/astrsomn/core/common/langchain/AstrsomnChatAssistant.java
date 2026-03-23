package org.astrsomn.core.common.langchain;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface AstrsomnChatAssistant {


    /**
     * 流式数据返回数据
     * @param message
     * @param memoryId
     * @return
     */
    TokenStream stream(@UserMessage String message, @MemoryId String memoryId);


    /**
     * 返回全文数据
     * @param message
     * @param memoryId
     * @return
     */
    String chat(@UserMessage String message, @MemoryId String memoryId);




}
