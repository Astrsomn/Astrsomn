package org.astrsomn.starter.langchain.memory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.entity.AiConversationEntity;
import org.astrsomn.core.common.langchain.ChatStreamEnum;
import org.astrsomn.core.mapper.AiConversationMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatMemoryManager {

    private final AiConversationMapper aiConversationMapper;
    private final Map<Object, ChatMemory> memoryCache = new ConcurrentHashMap<>();
    public ChatMemoryManager(AiConversationMapper aiConversationMapper) {
        this.aiConversationMapper = aiConversationMapper;
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
        AiConversationEntity entity = new AiConversationEntity();
        entity.setMemoryKey(memoryKey);
        entity.setContent(message.toString());


        if (message instanceof UserMessage) {
            entity.setRole(ChatStreamEnum.AstroChatRole.USER.getCode());
        } else if (message instanceof AiMessage) {
            entity.setRole(ChatStreamEnum.AstroChatRole.ASSISTANT.getCode());
        }

        aiConversationMapper.insert(entity);
    }

    private List<ChatMessage> loadHistoryFromDb(String memoryKey) {
        List<AiConversationEntity> entities = aiConversationMapper.selectList(
                new LambdaQueryWrapper<AiConversationEntity>()
                        .eq(AiConversationEntity::getMemoryKey, memoryKey)
                        .orderByAsc(AiConversationEntity::getCreateTime)
        );

        return entities.stream()
                .map(this::mapToChatMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ChatMessage mapToChatMessage(AiConversationEntity entity) {
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
