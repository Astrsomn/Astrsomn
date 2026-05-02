package com.astrsomn.starter.runtime.langchain.stream;

import com.astrsomn.api.runtime.common.constant.AiChatEnum;
import com.astrsomn.api.runtime.common.entity.AiChatSessionEntity;
import dev.langchain4j.model.output.TokenUsage;
import lombok.RequiredArgsConstructor;
import com.astrsomn.api.runtime.common.entity.AiChatMessageEntity;
import com.astrsomn.api.runtime.common.langchain.AstroHistoryRecorder;
import com.astrsomn.api.runtime.common.langchain.ChatStreamEnum;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.mapper.AiChatSessionMapper;
import com.astrsomn.starter.runtime.mapper.AiChatMessageMapper;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DatabaseHistoryRecorder implements AstroHistoryRecorder {

    private final AiChatMessageMapper mapper;
    private final AiChatSessionMapper sessionMapper;
    private final AstrsomnProperties astrsomnProperties;


    @Override
    @Transactional
    public void savePair(AstroChatParam param, String content, TokenUsage usage) {
        ensureSession(param);

        int baseOrder = mapper.getMaxMessageOrder(param.getMemoryKey());
        int inputTokens = usage != null ? usage.inputTokenCount() : 0;
        int outputTokens = usage != null ? usage.outputTokenCount() : 0;
        int turnNo = (baseOrder < 0 ? 0 : (baseOrder / 2)) + 1;

        AiChatMessageEntity user = createEntity(
                param,
                ChatStreamEnum.AstroChatRole.USER,
                param.getUserMessage(),
                turnNo,
                baseOrder + 1,
                inputTokens,
                0);
        AiChatMessageEntity assistant = createEntity(
                param,
                ChatStreamEnum.AstroChatRole.ASSISTANT,
                content,
                turnNo,
                baseOrder + 2,
                0,
                outputTokens);

        mapper.insert(user);
        mapper.insert(assistant);
        updateSessionStats(param, content, inputTokens, outputTokens);
    }

    private void ensureSession(AstroChatParam<?> param) {
        String memoryKey = param.getMemoryKey();
        if (StringUtils.isBlank(memoryKey)) {
            return;
        }
        String envCode = astrsomnProperties.getEnvCode();
        LambdaQueryWrapper<AiChatSessionEntity> wrapper = new LambdaQueryWrapper<AiChatSessionEntity>()
                .eq(AiChatSessionEntity::getMemoryKey, memoryKey)
                .last("LIMIT 1");
        if (StringUtils.isNotBlank(envCode)) {
            wrapper.eq(AiChatSessionEntity::getEnvCode, envCode);
        }
        AiChatSessionEntity existing = sessionMapper.selectOne(wrapper);
        if (existing != null) {
            return;
        }

        AiChatSessionEntity session = new AiChatSessionEntity();
        session.setMemoryKey(memoryKey);
        session.setSessionTitle(buildSessionTitle(param.getUserMessage()));
        session.setSessionStatus(AiChatEnum.SessionStatusEnum.ACTIVE.getCode());
        session.setLastMessagePreview(buildSessionTitle(param.getUserMessage()));
        session.setLastMessageAt(System.currentTimeMillis());
        session.setMessageCount(0);
        session.setPromptTokens(0);
        session.setCompletionTokens(0);
        session.setTotalTokens(0);
        session.setAgentKey(param.getAgentKey());
        session.setModelKey(param.getModelKey());
        session.setPromptKey(param.getPromptSetting().getPromptKey());
        session.setInstanceKey(param.getInstanceKey());
        session.setAccountKey(param.getModelSetting().getAccountKey());
        if (StringUtils.isNotBlank(envCode)) {
            session.setEnvCode(envCode);
        }
        sessionMapper.insert(session);
    }

    private void updateSessionStats(AstroChatParam<?> param, String content, int inputTokens, int outputTokens) {
        String envCode = astrsomnProperties.getEnvCode();
        String preview = buildSessionTitle(content);
        int totalTokens = Math.max(0, inputTokens) + Math.max(0, outputTokens);

        LambdaUpdateWrapper<AiChatSessionEntity> wrapper = new LambdaUpdateWrapper<AiChatSessionEntity>()
                .eq(AiChatSessionEntity::getMemoryKey, param.getMemoryKey());
        if (StringUtils.isNotBlank(envCode)) {
            wrapper.eq(AiChatSessionEntity::getEnvCode, envCode);
        }
        wrapper.setSql("MESSAGE_COUNT = COALESCE(MESSAGE_COUNT, 0) + 2");
        wrapper.setSql("PROMPT_TOKENS = COALESCE(PROMPT_TOKENS, 0) + " + Math.max(0, inputTokens));
        wrapper.setSql("COMPLETION_TOKENS = COALESCE(COMPLETION_TOKENS, 0) + " + Math.max(0, outputTokens));
        wrapper.setSql("TOTAL_TOKENS = COALESCE(TOTAL_TOKENS, 0) + " + totalTokens);
        wrapper.set(AiChatSessionEntity::getLastMessageAt, System.currentTimeMillis());
        wrapper.set(AiChatSessionEntity::getLastMessagePreview, preview);
        wrapper.set(AiChatSessionEntity::getAgentKey, param.getAgentKey());
        wrapper.set(AiChatSessionEntity::getModelKey, param.getModelKey());
        wrapper.set(AiChatSessionEntity::getPromptKey, param.getPromptSetting().getPromptKey());
        wrapper.set(AiChatSessionEntity::getInstanceKey, param.getInstanceKey());
        wrapper.set(AiChatSessionEntity::getAccountKey, param.getModelSetting().getAccountKey());
        sessionMapper.update(null, wrapper);
    }

    private String buildSessionTitle(String content) {
        if (StringUtils.isBlank(content)) {
            return "New Chat";
        }
        String trimmed = content.trim();
        return trimmed.length() > 80 ? trimmed.substring(0, 80) : trimmed;
    }

    private AiChatMessageEntity createEntity(
            AstroChatParam<?> param,
            ChatStreamEnum.AstroChatRole role,
            String message,
            int turnNo,
            int messageOrder,
            int promptTokens,
            int completionTokens) {
        AiChatMessageEntity entity = new AiChatMessageEntity();
        String memoryKey = param.getMemoryKey();

        entity.setMemoryKey(memoryKey);
        entity.setTurnNo(turnNo);
        entity.setRole(role.getCode());
        entity.setMessageType(AiChatEnum.MessageTypeEnum.TEXT.getCode());
        entity.setContent(message);
        entity.setMessageOrder(messageOrder);
        entity.setResponseStatus(AiChatEnum.ResponseStatusEnum.COMPLETED.getCode());
        entity.setPromptTokens(Math.max(0, promptTokens));
        entity.setCompletionTokens(Math.max(0, completionTokens));
        entity.setTotalTokens(Math.max(0, promptTokens) + Math.max(0, completionTokens));
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
