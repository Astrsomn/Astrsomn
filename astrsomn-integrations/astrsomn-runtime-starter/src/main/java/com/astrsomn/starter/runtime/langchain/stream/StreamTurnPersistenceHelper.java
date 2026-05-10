package com.astrsomn.starter.runtime.langchain.stream;

import com.astrsomn.api.runtime.common.constant.AiChatEnum;
import com.astrsomn.api.runtime.common.dto.chat.message.ext.AiChatMessageExtJsonKeys;
import com.astrsomn.api.runtime.common.entity.AiChatMessageEntity;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.common.utils.JsonUtil;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.langchain.quota.SensitiveWordProvider;
import com.astrsomn.starter.runtime.mapper.AiChatMessageMapper;
import com.astrsomn.starter.runtime.mapper.AiChatSessionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 将一轮流式对话（思考、工具、正文、错误）写入 {@code AI_CHAT_MESSAGE}。
 */
@Service
@RequiredArgsConstructor
public class StreamTurnPersistenceHelper {

    private final AiChatMessageMapper mapper;
    private final AiChatSessionMapper sessionMapper;
    private final AstrsomnProperties astrsomnProperties;
    private final SensitiveWordProvider sensitiveWordProvider;

    @Transactional
    public void persistStreamTurn(
            AstroChatParam<?> param,
            String runId,
            StreamTurnBuffer buffer,
            TokenUsage usage,
            Throwable error,
            boolean interrupted) {
        if (!param.isEnableHistorySave() || StringUtils.isBlank(param.getMemoryKey())) {
            return;
        }

        ensureSession(param);

        String envCode = astrsomnProperties.getEnvCode();
        int baseOrder = mapper.getMaxMessageOrder(param.getMemoryKey(), blankToNull(envCode));
        int turnNo = mapper.getMaxTurnNo(param.getMemoryKey(), blankToNull(envCode)) + 1;
        AtomicInteger order = new AtomicInteger(baseOrder + 1);

        int inputTokens = usage != null ? usage.inputTokenCount() : 0;
        int outputTokens = usage != null ? usage.outputTokenCount() : 0;

        List<StreamTurnBuffer.Segment> timeline = buffer.snapshotSegments();
        int lastTextSegmentIndex = -1;
        for (int i = timeline.size() - 1; i >= 0; i--) {
            StreamTurnBuffer.Segment s = timeline.get(i);
            if (s.getKind() == StreamTurnBuffer.Kind.TEXT) {
                String t = sensitiveWordProvider.filter(s.textContent());
                if (StringUtils.isNotBlank(t)) {
                    lastTextSegmentIndex = i;
                    break;
                }
            }
        }

        List<AiChatMessageEntity> rows = new ArrayList<>();

        rows.add(buildUserRow(param, runId, turnNo, order.getAndIncrement(), inputTokens, envCode));

        AiChatEnum.ResponseStatusEnum segmentStatus = interrupted
                ? AiChatEnum.ResponseStatusEnum.INTERRUPTED
                : AiChatEnum.ResponseStatusEnum.COMPLETED;

        for (int i = 0; i < timeline.size(); i++) {
            StreamTurnBuffer.Segment seg = timeline.get(i);
            if (seg.getKind() == StreamTurnBuffer.Kind.THINKING) {
                String safe = sensitiveWordProvider.filter(seg.textContent());
                if (StringUtils.isNotBlank(safe)) {
                    rows.add(buildAssistantRow(
                            param,
                            runId,
                            turnNo,
                            order.getAndIncrement(),
                            AiChatEnum.MessageTypeEnum.REASONING,
                            safe,
                            AiChatEnum.MessagePartKindEnum.THOUGHT.getCode(),
                            segmentStatus,
                            null,
                            0,
                            0,
                            envCode));
                }
            } else if (seg.getKind() == StreamTurnBuffer.Kind.TEXT) {
                String safe = sensitiveWordProvider.filter(seg.textContent());
                if (StringUtils.isNotBlank(safe)) {
                    int comp = (i == lastTextSegmentIndex) ? outputTokens : 0;
                    rows.add(buildAssistantRow(
                            param,
                            runId,
                            turnNo,
                            order.getAndIncrement(),
                            AiChatEnum.MessageTypeEnum.TEXT,
                            safe,
                            AiChatEnum.MessagePartKindEnum.TEXT.getCode(),
                            segmentStatus,
                            null,
                            0,
                            comp,
                            envCode));
                }
            } else if (seg.getKind() == StreamTurnBuffer.Kind.TOOL) {
                ToolExecution te = seg.getTool();
                if (te == null) {
                    continue;
                }
                String toolName = te.request() != null ? te.request().name() : "";
                String result = te.result() != null ? String.valueOf(te.result()) : "";
                ToolPersistKind kind = classifyTool(toolName);
                if (kind == ToolPersistKind.IMAGE) {
                    rows.add(buildAssistantRow(
                            param,
                            runId,
                            turnNo,
                            order.getAndIncrement(),
                            AiChatEnum.MessageTypeEnum.IMAGE,
                            result,
                            AiChatEnum.MessagePartKindEnum.IMAGE.getCode(),
                            segmentStatus,
                            toolName,
                            0,
                            0,
                            envCode));
                } else if (kind == ToolPersistKind.HTML) {
                    rows.add(buildAssistantRow(
                            param,
                            runId,
                            turnNo,
                            order.getAndIncrement(),
                            AiChatEnum.MessageTypeEnum.HTML,
                            result,
                            AiChatEnum.MessagePartKindEnum.HTML.getCode(),
                            segmentStatus,
                            toolName,
                            0,
                            0,
                            envCode));
                } else {
                    rows.add(buildToolResultRow(
                            param,
                            runId,
                            turnNo,
                            order.getAndIncrement(),
                            toolName,
                            result,
                            segmentStatus,
                            envCode));
                }
            }
        }

        if (error != null) {
            String errMsg = error.getMessage() != null ? error.getMessage().trim() : "";
            if (errMsg.isEmpty()) {
                errMsg = error.getClass().getSimpleName();
            }
            rows.add(buildAssistantRow(
                    param,
                    runId,
                    turnNo,
                    order.getAndIncrement(),
                    AiChatEnum.MessageTypeEnum.ERROR,
                    errMsg,
                    AiChatEnum.MessagePartKindEnum.ERROR.getCode(),
                    AiChatEnum.ResponseStatusEnum.FAILED,
                    AiChatEnum.FinishReasonEnum.ERROR.getCode(),
                    0,
                    0,
                    envCode));
        }

        for (AiChatMessageEntity row : rows) {
            mapper.insert(row);
        }

        String previewText = lastPlainAssistantPreview(timeline, error);
        updateSessionStats(param, previewText, errPreview(error, previewText), inputTokens, outputTokens, rows.size(), envCode);
    }

    private String lastPlainAssistantPreview(List<StreamTurnBuffer.Segment> timeline, Throwable error) {
        if (error != null) {
            return errPreview(error, "");
        }
        for (int i = timeline.size() - 1; i >= 0; i--) {
            StreamTurnBuffer.Segment s = timeline.get(i);
            if (s.getKind() == StreamTurnBuffer.Kind.TEXT) {
                String t = sensitiveWordProvider.filter(s.textContent());
                if (StringUtils.isNotBlank(t)) {
                    return t;
                }
            }
        }
        return "";
    }

    private static String errPreview(Throwable error, String safeAssistant) {
        if (error != null) {
            String m = error.getMessage() != null ? error.getMessage().trim() : "";
            return m.isEmpty() ? error.getClass().getSimpleName() : m;
        }
        return safeAssistant;
    }

    private enum ToolPersistKind {
        IMAGE, HTML, GENERIC
    }

    private static ToolPersistKind classifyTool(String toolName) {
        if (toolName == null) {
            return ToolPersistKind.GENERIC;
        }
        String n = toolName.toLowerCase();
        if (n.startsWith("image") || n.contains("_image") || n.contains("generateimage")) {
            return ToolPersistKind.IMAGE;
        }
        if (n.contains("html")) {
            return ToolPersistKind.HTML;
        }
        return ToolPersistKind.GENERIC;
    }

    private void ensureSession(AstroChatParam<?> param) {
        String memoryKey = param.getMemoryKey();
        if (StringUtils.isBlank(memoryKey)) {
            return;
        }
        String envCode = astrsomnProperties.getEnvCode();
        LambdaQueryWrapper<com.astrsomn.api.runtime.common.entity.AiChatSessionEntity> wrapper =
                new LambdaQueryWrapper<com.astrsomn.api.runtime.common.entity.AiChatSessionEntity>()
                        .eq(com.astrsomn.api.runtime.common.entity.AiChatSessionEntity::getMemoryKey, memoryKey)
                        .last("LIMIT 1");
        if (StringUtils.isNotBlank(envCode)) {
            wrapper.eq(com.astrsomn.api.runtime.common.entity.AiChatSessionEntity::getEnvCode, envCode);
        }
        if (sessionMapper.selectOne(wrapper) != null) {
            return;
        }

        com.astrsomn.api.runtime.common.entity.AiChatSessionEntity session =
                new com.astrsomn.api.runtime.common.entity.AiChatSessionEntity();
        session.setMemoryKey(memoryKey);
        String title = buildSessionTitle(previewUserInput(param));
        session.setSessionTitle(title);
        session.setSessionStatus(AiChatEnum.SessionStatusEnum.ACTIVE.getCode());
        session.setLastMessagePreview(title);
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

    private AiChatMessageEntity buildUserRow(
            AstroChatParam<?> param,
            String runId,
            int turnNo,
            int messageOrder,
            int promptTokens,
            String envCode) {
        AiChatMessageEntity e = baseEntity(param, turnNo, messageOrder, envCode);
        e.setRole(AiChatEnum.RoleEnum.USER.getCode());
        e.setMessageType(AiChatEnum.MessageTypeEnum.TEXT.getCode());
        String userText = previewUserInput(param);
        e.setContent(userText != null ? userText : "");
        e.setPromptTokens(Math.max(0, promptTokens));
        e.setCompletionTokens(0);
        e.setTotalTokens(Math.max(0, promptTokens));
        e.setResponseStatus(AiChatEnum.ResponseStatusEnum.COMPLETED.getCode());
        e.setExtJson(buildUserExtJson(param, runId));
        return e;
    }

    private String buildUserExtJson(AstroChatParam<?> param, String runId) {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put(AiChatMessageExtJsonKeys.SCHEMA_VERSION, AiChatMessageExtJsonKeys.SCHEMA_VERSION_V1);
        root.put(AiChatMessageExtJsonKeys.RUN_ID, runId);
        root.put(AiChatMessageExtJsonKeys.LINEAGE, AiChatEnum.MessageLineageEnum.USER_TURN_ROOT.getCode());
        List<String> files = param.getFileUrlList();
        if (files != null && !files.isEmpty()) {
            List<Map<String, Object>> attachments = new ArrayList<>();
            for (String url : files) {
                if (StringUtils.isBlank(url)) {
                    continue;
                }
                Map<String, Object> one = new LinkedHashMap<>();
                one.put("kind", AiChatEnum.AttachmentKindEnum.IMAGE.getCode());
                one.put("url", url.trim());
                attachments.add(one);
            }
            if (!attachments.isEmpty()) {
                root.put(AiChatMessageExtJsonKeys.ATTACHMENTS, attachments);
            }
        }
        return JsonUtil.toJson(root);
    }

    private AiChatMessageEntity buildAssistantRow(
            AstroChatParam<?> param,
            String runId,
            int turnNo,
            int messageOrder,
            AiChatEnum.MessageTypeEnum messageType,
            String content,
            String partKind,
            AiChatEnum.ResponseStatusEnum status,
            String finishReason,
            int promptTokens,
            int completionTokens,
            String envCode) {
        AiChatMessageEntity e = baseEntity(param, turnNo, messageOrder, envCode);
        e.setRole(AiChatEnum.RoleEnum.ASSISTANT.getCode());
        e.setMessageType(messageType.getCode());
        e.setContent(content);
        e.setPromptTokens(Math.max(0, promptTokens));
        e.setCompletionTokens(Math.max(0, completionTokens));
        e.setTotalTokens(Math.max(0, promptTokens) + Math.max(0, completionTokens));
        e.setResponseStatus(status.getCode());
        e.setFinishReason(finishReason);
        e.setExtJson(buildSegmentExtJson(runId, partKind, null));
        return e;
    }

    private AiChatMessageEntity buildToolResultRow(
            AstroChatParam<?> param,
            String runId,
            int turnNo,
            int messageOrder,
            String toolName,
            String result,
            AiChatEnum.ResponseStatusEnum status,
            String envCode) {
        AiChatMessageEntity e = baseEntity(param, turnNo, messageOrder, envCode);
        e.setRole(AiChatEnum.RoleEnum.TOOL.getCode());
        e.setMessageType(AiChatEnum.MessageTypeEnum.TOOL_RESULT.getCode());
        e.setContent(result);
        e.setPromptTokens(0);
        e.setCompletionTokens(0);
        e.setTotalTokens(0);
        e.setResponseStatus(status.getCode());
        Map<String, Object> ext = new LinkedHashMap<>();
        ext.put(AiChatMessageExtJsonKeys.SCHEMA_VERSION, AiChatMessageExtJsonKeys.SCHEMA_VERSION_V1);
        ext.put(AiChatMessageExtJsonKeys.RUN_ID, runId);
        ext.put(AiChatMessageExtJsonKeys.LINEAGE, AiChatEnum.MessageLineageEnum.TOOL_RESULT.getCode());
        ext.put(AiChatMessageExtJsonKeys.PART_KIND, AiChatEnum.MessagePartKindEnum.TOOL_RESULT.getCode());
        ext.put(AiChatMessageExtJsonKeys.TOOL_NAME, toolName);
        e.setExtJson(JsonUtil.toJson(ext));
        return e;
    }

    private static String buildSegmentExtJson(String runId, String partKind, String toolName) {
        Map<String, Object> ext = new LinkedHashMap<>();
        ext.put(AiChatMessageExtJsonKeys.SCHEMA_VERSION, AiChatMessageExtJsonKeys.SCHEMA_VERSION_V1);
        ext.put(AiChatMessageExtJsonKeys.RUN_ID, runId);
        ext.put(AiChatMessageExtJsonKeys.LINEAGE, AiChatEnum.MessageLineageEnum.ASSISTANT_SEGMENT.getCode());
        ext.put(AiChatMessageExtJsonKeys.PART_KIND, partKind);
        if (StringUtils.isNotBlank(toolName)) {
            ext.put(AiChatMessageExtJsonKeys.TOOL_NAME, toolName);
        }
        return JsonUtil.toJson(ext);
    }

    private AiChatMessageEntity baseEntity(AstroChatParam<?> param, int turnNo, int messageOrder, String envCode) {
        AiChatMessageEntity entity = new AiChatMessageEntity();
        entity.setMemoryKey(param.getMemoryKey());
        entity.setTurnNo(turnNo);
        entity.setMessageOrder(messageOrder);
        entity.setAgentKey(param.getAgentKey());
        entity.setModelKey(param.getModelKey());
        entity.setPromptKey(param.getPromptSetting().getPromptKey());
        entity.setInstanceKey(param.getInstanceKey());
        entity.setAccountKey(param.getModelSetting().getAccountKey());
        if (StringUtils.isNotBlank(envCode)) {
            entity.setEnvCode(envCode);
        }
        return entity;
    }

    private void updateSessionStats(
            AstroChatParam<?> param,
            String assistantPreview,
            String lastPreview,
            int inputTokens,
            int outputTokens,
            int rowCount,
            String envCode) {
        String preview = buildSessionTitle(StringUtils.isNotBlank(lastPreview) ? lastPreview : assistantPreview);
        int totalTokens = Math.max(0, inputTokens) + Math.max(0, outputTokens);

        LambdaUpdateWrapper<com.astrsomn.api.runtime.common.entity.AiChatSessionEntity> wrapper =
                new LambdaUpdateWrapper<com.astrsomn.api.runtime.common.entity.AiChatSessionEntity>()
                        .eq(com.astrsomn.api.runtime.common.entity.AiChatSessionEntity::getMemoryKey, param.getMemoryKey());
        if (StringUtils.isNotBlank(envCode)) {
            wrapper.eq(com.astrsomn.api.runtime.common.entity.AiChatSessionEntity::getEnvCode, envCode);
        }
        wrapper.setSql("MESSAGE_COUNT = COALESCE(MESSAGE_COUNT, 0) + " + Math.max(0, rowCount));
        wrapper.setSql("PROMPT_TOKENS = COALESCE(PROMPT_TOKENS, 0) + " + Math.max(0, inputTokens));
        wrapper.setSql("COMPLETION_TOKENS = COALESCE(COMPLETION_TOKENS, 0) + " + Math.max(0, outputTokens));
        wrapper.setSql("TOTAL_TOKENS = COALESCE(TOTAL_TOKENS, 0) + " + totalTokens);
        wrapper.set(com.astrsomn.api.runtime.common.entity.AiChatSessionEntity::getLastMessageAt, System.currentTimeMillis());
        wrapper.set(com.astrsomn.api.runtime.common.entity.AiChatSessionEntity::getLastMessagePreview, preview);
        wrapper.set(com.astrsomn.api.runtime.common.entity.AiChatSessionEntity::getAgentKey, param.getAgentKey());
        wrapper.set(com.astrsomn.api.runtime.common.entity.AiChatSessionEntity::getModelKey, param.getModelKey());
        wrapper.set(com.astrsomn.api.runtime.common.entity.AiChatSessionEntity::getPromptKey, param.getPromptSetting().getPromptKey());
        wrapper.set(com.astrsomn.api.runtime.common.entity.AiChatSessionEntity::getInstanceKey, param.getInstanceKey());
        wrapper.set(com.astrsomn.api.runtime.common.entity.AiChatSessionEntity::getAccountKey, param.getModelSetting().getAccountKey());
        sessionMapper.update(null, wrapper);
    }

    private static String previewUserInput(AstroChatParam<?> param) {
        return StringUtils.trimToNull(param.getUserMessageText());
    }

    private String buildSessionTitle(String content) {
        if (StringUtils.isBlank(content)) {
            return "New Chat";
        }
        String trimmed = content.trim();
        return trimmed.length() > 80 ? trimmed.substring(0, 80) : trimmed;
    }

    private static String blankToNull(String envCode) {
        return StringUtils.isBlank(envCode) ? null : envCode;
    }
}
