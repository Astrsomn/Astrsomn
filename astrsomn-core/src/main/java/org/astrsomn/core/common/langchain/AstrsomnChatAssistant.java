package org.astrsomn.core.common.langchain;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface AstrsomnChatAssistant {


    /**
     *
     * @param message
     * @param memoryId
     * @return
     */
    TokenStream stream(@UserMessage String message, @MemoryId String memoryId);


    /**
     *
     * @param message
     * @param memoryId
     * @return
     */
    String chat(@UserMessage String message, @MemoryId String memoryId);




}
