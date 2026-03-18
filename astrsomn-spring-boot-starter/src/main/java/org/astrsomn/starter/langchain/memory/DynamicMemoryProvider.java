package org.astrsomn.starter.langchain.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.astrsomn.starter.langchain.memory.DynamicMemoryProvider;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class DynamicMemoryProvider implements ChatMemoryProvider {

    private final AiConversationFacade aiConversationFacade;

    private Integer maxMessages;

    @Autowired
    public DynamicMemoryProvider(AiConversationFacade aiConversationFacade) {
        this.aiConversationFacade = aiConversationFacade;
    }

    @Override
    public ChatMemory get(Object memoryId) {
        String idStr = Optional.ofNullable(memoryId)
                .map(Object::toString)
                .orElseThrow(() -> new IllegalArgumentException("内存 ID 不能为空"));

        log.info("正在加载 ID 为 {} 的动态记忆", idStr);

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(maxMessages)
                .build();

        try {
            List<ChatMessage> history = aiConversationFacade.loadFromDatabase(idStr);

            if (history != null && !history.isEmpty()) {
                log.debug("成功从数据库恢复 memoryId: {} 的 {} 条历史消息", idStr, history.size());
                history.forEach(chatMemory::add);
            }
        } catch (Exception e) {
            log.error("加载 memoryId: {} 的历史消息失败", idStr, e);
        }

        return chatMemory;
    }

    public void initialize(Integer maxMessages) {
        this.maxMessages = maxMessages;
    }
}