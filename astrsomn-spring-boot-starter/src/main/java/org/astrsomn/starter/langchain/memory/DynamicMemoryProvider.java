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
import org.astrsomn.core.mapper.AiConversationMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class DynamicMemoryProvider implements ChatMemoryProvider {

    private final AiConversationMapper aiConversationMapper;

    private Integer maxMessages;


    public DynamicMemoryProvider(AiConversationMapper aiConversationMapper) {
        this.aiConversationMapper = aiConversationMapper;
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
            List<ChatMessage> history = this.loadFromDatabase(idStr);

            if (history != null && !history.isEmpty()) {
                log.debug("成功从数据库恢复 memoryId: {} 的 {} 条历史消息", idStr, history.size());
                history.forEach(chatMemory::add);
            }
        } catch (Exception e) {
            log.error("加载 memoryId: {} 的历史消息失败", idStr, e);
        }

        return chatMemory;
    }

    private List<ChatMessage> loadFromDatabase(String idStr) {
        List<String> ids = Arrays.asList(idStr.split(","));
        List<AiConversationEntity> list = aiConversationMapper.selectList(new LambdaQueryWrapper<AiConversationEntity>()
                .in(AiConversationEntity::getId, ids));

      return   list.stream()
                .map(dto -> {
                    // 根据 DTO 中的角色字段来判断创建 UserMessage 还是 AiMessage
                    // 注意：这里需要根据你实际的 DTO 结构调整角色判断逻辑 (例如: "user", "assistant")
                    if ("user".equalsIgnoreCase(dto.getRole())) {
                        return new UserMessage(dto.getContent());
                    } else {
                        return new AiMessage(dto.getContent());
                    }
                })
                .collect(Collectors.toList());


    }

    public void initialize(Integer maxMessages) {
        this.maxMessages = maxMessages;
    }
}