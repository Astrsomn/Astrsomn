package org.astrsomn.starter.langchain.stream;



import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.astrsomn.core.common.util.JsonUtil;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.common.langchain.ChatStreamEnum;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.starter.langchain.quota.ModelQuotaManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Map;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class AstroChatStreamUtil {


    private final DatabaseHistoryRecorder historyRecorder;
    private final ModelQuotaManager modelQuotaManager;

    public Flux<String> convertStreamToFlux(TokenStream inputStream, AstroChatParam chatParam) {
        // 用于记录全文内容
        AtomicReference<StringBuilder> contentBuilder = new AtomicReference<>(new StringBuilder());

        AtomicReference<TokenUsage> usageRef = new AtomicReference<>();
        return Flux.create(fluxSink -> {
            AtomicBoolean isCancelled = new AtomicBoolean(false);
            inputStream.onPartialThinking(thinking -> {
                        if (isCancelled.get()) return;
                        log.warn("====>  思考过程:{}", thinking);
                        this.sendEvent(fluxSink, ChatStreamEnum.AstroEventType.THOUGHT, thinking.text());
                    })
                    .onPartialResponse(partial -> {
                        if (isCancelled.get()) return;
                        if (StringUtils.isNotBlank(partial)) {
                            contentBuilder.get().append(partial);
                            this.sendEvent(fluxSink, ChatStreamEnum.AstroEventType.TEXT, partial);
                        }
                    })
                    .onToolExecuted(toolExecution -> {
                        if (isCancelled.get()) return;
                        handleToolOutput(fluxSink, toolExecution);
                    })
                    .onCompleteResponse(complete -> {
                        if (isCancelled.get()) return;
                        usageRef.set(complete.tokenUsage());
                        sendEvent(fluxSink, ChatStreamEnum.AstroEventType.DONE, "[DONE]");
                        fluxSink.complete();
                    })
                    .onError(error -> {
                        if (isCancelled.get()) return;
                        log.error("Astro stream error", error);
                        fluxSink.error(error);
                    }).start();

            fluxSink.onCancel(() -> {
                isCancelled.set(true);
                log.warn("====> [Astrsomn] 检测到用户断开连接，已停止向客户端推送并标记忽略后续回调");
            });
        });
    }

    // SSE推送
    public void sendEvent(FluxSink<String> sink, ChatStreamEnum.AstroEventType eventType, String content) {
        if (content == null) return;
        log.info("Astro stream event: type={}, content={}", eventType.getCode(), content);
        sink.next(JsonUtil.toJson(Map.of(
                "type", eventType.getCode(),
                "content", content,
                "timestamp", System.currentTimeMillis()
        )));
    }
    // 处理工具调用时
    private void handleToolOutput(FluxSink<String> sink, ToolExecution toolExecution) {
        String toolName = toolExecution.request().name();
        if (toolName.startsWith(ChatStreamEnum.AstroEventType.HTML.getPrefix())) {
            sendEvent(sink, ChatStreamEnum.AstroEventType.HTML, toolExecution.result());
        }else if (toolName.startsWith(ChatStreamEnum.AstroEventType.IMAGE.getPrefix())) {
            sendEvent(sink, ChatStreamEnum.AstroEventType.IMAGE, toolExecution.result());
        }
    }















    /**
     * 将 Map 转换为 JSON 字符串
     * 专门针对基础封装设计，处理了转义、嵌套和常见数据类型
     */
    public static String toJson(Map<String, Object> map) {
        if (map == null) return "null";

        StringBuilder sb = new StringBuilder();
        serializeMap(map, sb);
        return sb.toString();
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

    /**
     * 关键：对字符串进行 JSON 标准转义
     */
    private static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
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



}
