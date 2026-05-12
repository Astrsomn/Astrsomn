package com.astrsomn.starter.runtime.langchain.memory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.api.runtime.common.entity.AiChatMessageEntity;
import com.astrsomn.api.runtime.common.langchain.ChatStreamEnum;
import com.astrsomn.starter.runtime.mapper.AstAiChatMessageMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatMemoryManager {

    private final AstAiChatMessageMapper aiChatMessageMapper;
    private final Map<Object, ChatMemory> memoryCache = new ConcurrentHashMap<>();
    public ChatMemoryManager(AstAiChatMessageMapper aiChatMessageMapper) {
        this.aiChatMessageMapper = aiChatMessageMapper;
    }

    public ChatMemory getOrCreateMemory(Object memoryId, int maxMessages) {
        return memoryCache.computeIfAbsent(memoryId, id -> {
            log.info("初始化会话记忆缓存: {}", id);

            ChatMemory chatMemory = MessageWindowChatMemory.builder()
                    .id(id)
                    .maxMessages(maxMessages)
                    .build();
            List<ChatMessage> history = loadHistoryFromDb(id.toString());
            history.forEach(chatMemory::add);
            return chatMemory;
        });
    }


    public void saveMessage(String memoryKey, ChatMessage message) {
        AiChatMessageEntity entity = new AiChatMessageEntity();
        entity.setMemoryKey(memoryKey);
        entity.setContent(message.toString());


        if (message instanceof UserMessage) {
            entity.setRole(ChatStreamEnum.AstroChatRole.USER.getCode());
        } else if (message instanceof AiMessage) {
            entity.setRole(ChatStreamEnum.AstroChatRole.ASSISTANT.getCode());
        }

        aiChatMessageMapper.insert(entity);
    }

    private List<ChatMessage> loadHistoryFromDb(String memoryKey) {
        List<AiChatMessageEntity> entities = aiChatMessageMapper.selectList(
                new LambdaQueryWrapper<AiChatMessageEntity>()
                        .eq(AiChatMessageEntity::getMemoryKey, memoryKey)
                        .orderByAsc(AiChatMessageEntity::getCreateTime)
        );

        return entities.stream()
                .map(this::mapToChatMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ChatMessage mapToChatMessage(AiChatMessageEntity entity) {
        String role = entity.getRole().toLowerCase();
        if (ChatStreamEnum.AstroChatRole.USER.getCode().equalsIgnoreCase(role)) {
            return UserMessage.from(entity.getContent());
        } else if (ChatStreamEnum.AstroChatRole.ASSISTANT.getCode().equalsIgnoreCase(role)) {
            return AiMessage.from(entity.getContent());
        }
        return null;
    }

    public void clearCache(Object memoryId) {
        memoryCache.remove(memoryId);
    }
}
