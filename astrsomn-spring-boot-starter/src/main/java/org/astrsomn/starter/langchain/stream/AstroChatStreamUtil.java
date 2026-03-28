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
import org.astrsomn.starter.langchain.quota.ModelQuotaManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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
        sink.next(JsonUtils.toJson(Map.of(
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


}
