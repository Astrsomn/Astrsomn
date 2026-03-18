package org.astrsomn.starter.langchain.tokenStream;

import dev.langchain4j.model.output.TokenUsage;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.astrsomn.core.common.entity.AiConversationEntity;
import org.astrsomn.core.common.langchain.buildParam.AiChatBuildParam;
import org.astrsomn.core.common.langchain.AstroHistoryRecorder;
import org.astrsomn.core.common.langchain.ChatStreamEnum;
import org.astrsomn.core.mapper.AiConversationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class DatabaseHistoryRecorder implements AstroHistoryRecorder {
    @Resource
    private final AiConversationMapper mapper;
    @Override
    @Transactional
    public void savePair(AiChatBuildParam param, String content, TokenUsage usage) {
        int baseOrder = mapper.getMaxMessageOrder(param.getMemoryId());

        // 使用枚举 AstroChatRole.USER
        AiConversationEntity user = createEntity(param, ChatStreamEnum.AstroChatRole.USER, param.getMessage(),
                baseOrder + 1, usage != null ? usage.inputTokenCount() : 0);

        // 使用枚举 AstroChatRole.ASSISTANT
        AiConversationEntity assistant = createEntity(param, ChatStreamEnum.AstroChatRole.ASSISTANT, content,
                baseOrder + 2, usage != null ? usage.outputTokenCount() : 0);

        mapper.insert(user);
        mapper.insert(assistant);
    }

    private AiConversationEntity createEntity(AiChatBuildParam param, ChatStreamEnum.AstroChatRole astroChatRole, String message, int i, int i1) {
        return null;
    }

}
