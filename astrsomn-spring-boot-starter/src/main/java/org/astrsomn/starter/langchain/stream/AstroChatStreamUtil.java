package org.astrsomn.starter.langchain.stream;


import com.alibaba.dashscope.utils.JsonUtils;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.astrsomn.core.common.langchain.ChatStreamEnum;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class AstroChatStreamUtil {

    private final Executor taskExecutor;
    private final DatabaseHistoryRecorder historyRecorder;

    public Flux<String> convertStreamToFlux(TokenStream inputStream, AstroChatParam chatParam) {
        // 用于记录全文内容
        AtomicReference<StringBuilder> contentBuilder = new AtomicReference<>(new StringBuilder());

        AtomicReference<TokenUsage> usageRef = new AtomicReference<>();
        return Flux.create(fluxSink -> {

            inputStream.onPartialThinking(thinking -> {
                        this.sendEvent(fluxSink, ChatStreamEnum.AstroEventType.THOUGHT, thinking.text());
                    })
                    .onPartialResponse(partial -> {
                        if (StringUtils.isNotBlank(partial)) {
                            this.sendEvent(fluxSink, ChatStreamEnum.AstroEventType.TEXT, partial);
                        }
                    })
                    .onToolExecuted(toolExecution -> {
                        handleToolOutput(fluxSink, toolExecution);
                    })
                    .onCompleteResponse(complete -> {
                        usageRef.set(complete.tokenUsage());
                        finalizeConversation(chatParam, contentBuilder.get().toString(), usageRef.get());
                        sendEvent(fluxSink, ChatStreamEnum.AstroEventType.DONE, "[DONE]");
                        fluxSink.complete();
                    })
                    .onError(error -> {

                    }).start();

            fluxSink.onCancel(() -> {
                log.warn("====>  [Astrsomn]  检测到用户断开连接，正在标记中断状态");
            });
        });
    }

    // SSE推送
    public void sendEvent(FluxSink<String> sink, ChatStreamEnum.AstroEventType eventType, String content) {
        if (content == null) return;
        log.info("Astro stream event: type={}, content={}", eventType.getCode(), content);
        sink.next(JsonUtils.toJson(Map.of(
                "type", eventType.getCode(),
                "content", content,
                "timestamp", System.currentTimeMillis()
        )));
    }
    // 处理工具调用时
    private void handleToolOutput(FluxSink<String> sink, ToolExecution toolExecution) {
        String toolName = toolExecution.request().name();
        if (toolName.startsWith(ChatStreamEnum.AstroEventType.HTML.getCode())) {
            sendEvent(sink, ChatStreamEnum.AstroEventType.HTML, toolExecution.result());
        }
    }

    // 保存历史对话
    private void finalizeConversation(AstroChatParam param, String content, TokenUsage usage) {
        if (param == null || param.getMemoryKey() == null) return;
        CompletableFuture.runAsync(() -> {
            try {
                historyRecorder.savePair(param, content, usage);
            } catch (Exception e) {
                log.error("Astro history save failed: {}", e.getMessage());
            }
        }, taskExecutor);
    }

}
