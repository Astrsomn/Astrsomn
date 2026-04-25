package com.astrsomn.starter.langchain.stream;

import dev.langchain4j.model.output.TokenUsage;
import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.entity.AiConversationEntity;
import com.astrsomn.core.common.langchain.AstroHistoryRecorder;
import com.astrsomn.core.common.langchain.ChatStreamEnum;
import com.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.starter.mapper.AiAgentMapper;
import com.astrsomn.starter.mapper.AiConversationMapper;
import com.astrsomn.starter.mapper.AiModelMapper;
import com.astrsomn.starter.config.AstrsomnProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DatabaseHistoryRecorder implements AstroHistoryRecorder {

    private final AiConversationMapper mapper;
    private final AiAgentMapper aiAgentMapper;
    private final AiModelMapper aiModelMapper;
    private final AstrsomnProperties astrsomnProperties;


    @Override
    @Transactional
    public void savePair(AstroChatParam param, String content, TokenUsage usage) {
        int baseOrder = mapper.getMaxMessageOrder(param.getMemoryKey());
        AiConversationEntity user = createEntity(param, ChatStreamEnum.AstroChatRole.USER, param.getUserMessage(),
                baseOrder + 1, usage != null ? usage.inputTokenCount() : 0);
        AiConversationEntity assistant = createEntity(param, ChatStreamEnum.AstroChatRole.ASSISTANT, content,
                baseOrder + 2, usage != null ? usage.outputTokenCount() : 0);

        mapper.insert(user);
        mapper.insert(assistant);
    }

    private AiConversationEntity createEntity(
            AstroChatParam<?> param,
            ChatStreamEnum.AstroChatRole role,
            String message,
            int messageOrder,
            int consumeTokens) {
        AiConversationEntity entity = new AiConversationEntity();
        String memoryKey = param.getMemoryKey();

        entity.setMemoryKey(memoryKey);
        entity.setRole(role.getCode());
        entity.setContent(message);
        entity.setMessageOrder(messageOrder);
        entity.setConsumeTokens(Math.max(0, consumeTokens));
        entity.setAgentKey(param.getAgentKey());
        entity.setModelKey(param.getModelKey());
        entity.setPromptKey(param.getPromptSetting().getPromptKey());
        entity.setInstanceKey(param.getInstanceKey());
        entity.setAccountKey(param.getModelSetting().getAccountKey());
        if (StringUtils.isNotBlank(astrsomnProperties.getEnvCode())) {
            entity.setEnvCode(astrsomnProperties.getEnvCode());
        }
        return entity;
    }

}
