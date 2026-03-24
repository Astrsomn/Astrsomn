package org.astrsomn.starter.langchain.memory;


import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicMemoryProvider implements ChatMemoryProvider {

    private final ChatMemoryManager memoryManager;
    private final int maxMessages;

    public DynamicMemoryProvider(ChatMemoryManager memoryManager, int maxMessages) {
        this.memoryManager = memoryManager;
        this.maxMessages = maxMessages;
    }

    @Override
    public ChatMemory get(Object memoryId) {
        return memoryManager.getOrCreateMemory(memoryId, maxMessages);
    }
}