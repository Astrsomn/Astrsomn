package com.astrsomn.starter.runtime.langchain.stream;


import com.astrsomn.api.runtime.common.langchain.ChatStreamEnum;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.common.utils.JsonUtil;
import com.astrsomn.common.utils.StringUtils;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnClass(dev.langchain4j.service.TokenStream.class)
public class AstroChatStreamUtil {

    private final StreamTurnPersistenceHelper streamTurnPersistenceHelper;

    
    private static ToolStreamKind classifyToolForStream(String toolName) {
        if (toolName == null) {
            return ToolStreamKind.GENERIC;
        }
        String n = toolName.toLowerCase();
        if (n.startsWith("image") || n.contains("_image") || n.contains("generateimage")) {
            return ToolStreamKind.IMAGE;
        }
        if (n.contains("html")) {
            return ToolStreamKind.HTML;
        }
        return ToolStreamKind.GENERIC;
    }

    private static void serializeMap(Map<?, ?> map, StringBuilder sb) {
        sb.append("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(",");

            // 处理 Key (强制转为字符串并转义)
            String key = String.valueOf(entry.getKey());
            sb.append("\"").append(escape(key)).append("\":");

            // 处理 Value
            serializeValue(entry.getValue(), sb);

            first = false;
        }
        sb.append("}");
    }

    private static void serializeValue(Object value, StringBuilder sb) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String) {
            sb.append("\"").append(escape((String) value)).append("\"");
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(value);
        } else if (value instanceof Map) {
            serializeMap((Map<?, ?>) value, sb);
        } else if (value instanceof Iterable) {
            serializeIterable((Iterable<?>) value, sb);
        } else if (value.getClass().isArray()) {
            serializeArray(value, sb);
        } else {
            // 对于未知对象，调用 toString 并转义
            sb.append("\"").append(escape(value.toString())).append("\"");
        }
    }

    private static void serializeIterable(Iterable<?> iterable, StringBuilder sb) {
        sb.append("[");
        boolean first = true;
        for (Object item : iterable) {
            if (!first) sb.append(",");
            serializeValue(item, sb);
            first = false;
        }
        sb.append("]");
    }

    private static void serializeArray(Object array, StringBuilder sb) {
        sb.append("[");
        int length = java.lang.reflect.Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) sb.append(",");
            serializeValue(java.lang.reflect.Array.get(array, i), sb);
        }
        sb.append("]");
    }

    
    private static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    // 过滤控制字符，确保 JSON 格式合法
                    if (ch < ' ') {
                        String hex = Integer.toHexString(ch);
                        sb.append("\\u").append("0000", 0, 4 - hex.length()).append(hex);
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

    public Flux<String> convertStreamToFlux(TokenStream inputStream, AstroChatParam chatParam) {
        String runId = UUID.randomUUID().toString();
        StreamTurnBuffer turnBuffer = new StreamTurnBuffer();

        AtomicReference<TokenUsage> usageRef = new AtomicReference<>();
        AtomicBoolean persisted = new AtomicBoolean(false);
        return Flux.create(fluxSink -> {
            AtomicBoolean isCancelled = new AtomicBoolean(false);

            inputStream.onPartialThinking(thinking -> {
                        if (isCancelled.get()) return;
                        String piece = thinking != null ? thinking.text() : null;
                        if (StringUtils.isNotBlank(piece)) {
                            turnBuffer.appendThinking(piece);
                        }
                        if (thinking != null) {
                            log.debug("====> [Astrsomn] 思考片段: {}", thinking);
                        }
                        this.sendEvent(fluxSink, ChatStreamEnum.AstroEventType.THOUGHT, piece);
                    })
                    .onPartialResponse(partial -> {
                        if (isCancelled.get()) return;
                        if (StringUtils.isNotBlank(partial)) {
                            turnBuffer.appendText(partial);
                            this.sendEvent(fluxSink, ChatStreamEnum.AstroEventType.TEXT, partial);
                        }
                    })
                    .onToolExecuted(toolExecution -> {
                        if (isCancelled.get()) return;
                        if (toolExecution != null) {
                            turnBuffer.addTool(toolExecution);
                        }
                        handleToolOutput(fluxSink, toolExecution);
                    })
                    .onCompleteResponse(complete -> {
                        if (isCancelled.get()) return;
                        usageRef.set(complete.tokenUsage());
                        tryPersist(chatParam, runId, turnBuffer, complete.tokenUsage(), null, false, persisted);
                        sendEvent(fluxSink, ChatStreamEnum.AstroEventType.DONE, "[DONE]");
                        fluxSink.complete();
                    })
                    .onError(error -> {
                        if (isCancelled.get()) {
                            fluxSink.error(error);
                            return;
                        }
                        log.error("Astro stream error", error);
                        tryPersist(chatParam, runId, turnBuffer, usageRef.get(), error, false, persisted);
                        String errText = error.getMessage() != null ? error.getMessage() : error.toString();
                        sendEvent(fluxSink, ChatStreamEnum.AstroEventType.ERROR, errText);
                        fluxSink.error(error);
                    }).start();

            fluxSink.onCancel(() -> {
                isCancelled.set(true);
                log.warn("====> [Astrsomn] 检测到用户断开连接，已停止向客户端推送并标记忽略后续回调");
                tryPersist(chatParam, runId, turnBuffer, usageRef.get(), null, true, persisted);
            });
        });
    }

    private void tryPersist(
            AstroChatParam<?> chatParam,
            String runId,
            StreamTurnBuffer turnBuffer,
            TokenUsage usage,
            Throwable error,
            boolean interrupted,
            AtomicBoolean persisted) {
        if (!chatParam.isEnableHistorySave()) {
            return;
        }
        if (!persisted.compareAndSet(false, true)) {
            return;
        }
        try {
            streamTurnPersistenceHelper.persistStreamTurn(
                    chatParam,
                    runId,
                    turnBuffer,
                    usage,
                    error,
                    interrupted);
            chatParam.markStreamTurnPersisted();
        } catch (Exception ex) {
            log.error("====> [Astrsomn] 流式对话落库失败", ex);
        }
    }

    // SSE推送
    public void sendEvent(FluxSink<String> sink, ChatStreamEnum.AstroEventType eventType, String content) {
        if (content == null) return;
//        log.info("Astro stream event: type={}, content={}", eventType.getCode(), content);
        sink.next(JsonUtil.toJson(Map.of(
                "type", eventType.getCode(),
                "content", content,
                "timestamp", System.currentTimeMillis()
        )));
    }

    
    private void handleToolOutput(FluxSink<String> sink, ToolExecution toolExecution) {
        if (toolExecution == null || toolExecution.request() == null) {
            return;
        }
        String toolName = toolExecution.request().name();
        if (toolName == null) {
            toolName = "";
        }
        String args = toolExecution.request().arguments();
        if (args == null) {
            args = "";
        }
        Object rawResult = toolExecution.result();
        String resultStr = rawResult != null ? String.valueOf(rawResult) : "";

        ToolStreamKind kind = classifyToolForStream(toolName);
        if (kind == ToolStreamKind.IMAGE) {
            sendEvent(sink, ChatStreamEnum.AstroEventType.IMAGE, resultStr);
        } else if (kind == ToolStreamKind.HTML) {
            sendEvent(sink, ChatStreamEnum.AstroEventType.HTML, resultStr);
        } else {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("toolName", toolName);
            payload.put("args", args);
            payload.put("result", resultStr);
            sendEvent(sink, ChatStreamEnum.AstroEventType.TOOL, JsonUtil.toJson(payload));
        }
    }

    private enum ToolStreamKind {
        IMAGE, HTML, GENERIC
    }


}
