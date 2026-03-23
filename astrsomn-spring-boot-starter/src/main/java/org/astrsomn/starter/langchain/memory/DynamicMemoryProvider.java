package org.astrsomn.starter.langchain.memory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.entity.AiConversationEntity;
import org.astrsomn.core.common.langchain.ChatStreamEnum;
import org.astrsomn.core.mapper.AiConversationMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        // 直接转发给 Manager 处理
        return memoryManager.getOrCreateMemory(memoryId, maxMessages);
    }
}