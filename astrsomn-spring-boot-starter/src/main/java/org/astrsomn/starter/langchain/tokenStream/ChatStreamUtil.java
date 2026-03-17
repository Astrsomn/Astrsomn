package org.astrsomn.starter.langchain.tokenStream;


import com.alibaba.dashscope.utils.JsonUtils;
import dev.langchain4j.model.openai.internal.ResponseHandle;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.entity.AiConversationEntity;
import org.astrsomn.core.common.langchain.AiChatBuildParam;
import org.astrsomn.core.common.langchain.ChatStreamEnum;
import org.astrsomn.core.mapper.AiConversationMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.alibaba.dashscope.utils.JsonUtils.toJson;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatStreamUtil {

    @Resource
    private final AiConversationMapper aiConversationMapper;

    @Resource
    private final Executor taskExecutor;

    private DatabaseHistoryRecorder historyRecorder;

    public Flux<String> convertStreamToFlux(TokenStream inputStream, AiChatBuildParam buildParam) {
        return Flux.create(fluxSink -> {
            AtomicReference<StringBuilder> contentBuilder = new AtomicReference<>(new StringBuilder());
            AtomicReference<TokenUsage> usageRef = new AtomicReference<>();
            // 【核心】定义一个取消标志位
            AtomicBoolean isCancelled = new AtomicBoolean(false);
             inputStream
                    .onPartialThinking(thinking -> {
                        // 使用枚举：AstroEventType.THOUGHT
                        sendEvent(fluxSink, ChatStreamEnum.AstroEventType.THOUGHT, thinking.text());
                    })
                    .onPartialResponse(partial -> {
                        if (StringUtils.hasLength(partial)) {
                            contentBuilder.get().append(partial);
                            sendEvent(fluxSink, ChatStreamEnum.AstroEventType.TEXT, partial);
                        }
                    })
                    .onToolExecuted(toolExecution -> {
                        // 处理工具逻辑：后期可以扩展为 ToolHandler 策略模式
                        handleToolOutput(fluxSink, toolExecution);
                    })
                    .onCompleteResponse(response -> {
                        usageRef.set(response.tokenUsage());

                        // 异步持久化
                        finalizeConversation(buildParam, contentBuilder.get().toString(), usageRef.get());

                        // 发送完成信号（可选，前端根据这个判断结束）
                        sendEvent(fluxSink, ChatStreamEnum.AstroEventType.DONE, "[DONE]");
                        fluxSink.complete();
                    })
                    .onError(err -> {
                        sendEvent(fluxSink, ChatStreamEnum.AstroEventType.ERROR, err.getMessage());
                        fluxSink.error(err);
                    })
                    .start();


            // 在这里绑定取消逻辑
            fluxSink.onCancel(() -> {
                log.warn("检测到用户断开连接，正在标记中断状态...");
                isCancelled.set(true);
            });
        });
    }

    private void handleToolOutput(FluxSink<String> sink, ToolExecution toolExecution) {
        String toolName = toolExecution.request().name();
        if (toolName.startsWith(ChatStreamEnum.AstroEventType.HTML.getCode())) {
            sendEvent(sink, ChatStreamEnum.AstroEventType.HTML, toolExecution.result());
        }
    }

    private void sendEvent(FluxSink<String> sink, ChatStreamEnum.AstroEventType type, String content) {
        if (content == null) return;
        sink.next(JsonUtils.toJson(Map.of(
                "type", type.getCode(),
                "content", content,
                "timestamp", System.currentTimeMillis() // 增加时间戳，方便前端排序
        )));
    }

    private void finalizeConversation(AiChatBuildParam param, String content, TokenUsage usage) {
        if (param == null || param.getMemoryId() == null) return;

        // 异步任务，不阻塞流响应
        CompletableFuture.runAsync(() -> {
            try {
                historyRecorder.savePair(param, content, usage);
            } catch (Exception e) {
                log.error("Astro history save failed: {}", e.getMessage());
            }
        }, taskExecutor);
    }




}
