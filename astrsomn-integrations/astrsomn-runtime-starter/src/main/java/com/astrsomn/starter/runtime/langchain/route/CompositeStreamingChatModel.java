package com.astrsomn.starter.runtime.langchain.route;

import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelEndpoint;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelRouteSetting;
import com.astrsomn.starter.runtime.langchain.exception.AllEndpointsFailedException;
import com.astrsomn.starter.runtime.langchain.exception.NoAvailableEndpointException;
import dev.langchain4j.model.ModelProvider;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流式模型组合：在「建立流」阶段做选点与故障转移；收到任意分片后不再切换节点。
 * 不在此路径套用 {@link ResilienceDecorationStrategy}（异步分片与熔断语义不匹配）。
 */
public class CompositeStreamingChatModel implements StreamingChatModel {

    private final List<StreamingChatModel> delegates;
    private final List<ModelEndpoint> endpoints;
    private final ModelRouteSetting route;
    private final ModelRouteContext ctx;
    private final EndpointSelectionStrategy selection;
    private final List<ChatModelListener> outerListeners;
    private final AtomicInteger monotonicCall = new AtomicInteger(0);

    public CompositeStreamingChatModel(
            List<StreamingChatModel> delegates,
            List<ModelEndpoint> endpoints,
            ModelRouteSetting route,
            ModelRouteContext ctx,
            EndpointSelectionStrategy selection,
            List<ChatModelListener> outerListeners) {
        this.delegates = List.copyOf(delegates);
        this.endpoints = List.copyOf(endpoints);
        this.route = route;
        this.ctx = ctx;
        this.selection = selection;
        this.outerListeners = outerListeners == null ? List.of() : List.copyOf(outerListeners);
    }

    @Override
    public ChatRequestParameters defaultRequestParameters() {
        return delegates.get(0).defaultRequestParameters();
    }

    @Override
    public List<ChatModelListener> listeners() {
        return outerListeners;
    }

    @Override
    public ModelProvider provider() {
        return delegates.get(0).provider();
    }

    @Override
    public void doChat(ChatRequest chatRequest, StreamingChatResponseHandler handler) {
        startAttempt(chatRequest, handler, 0, new HashSet<>(), monotonicCall.getAndIncrement());
    }

    private void startAttempt(
            ChatRequest chatRequest,
            StreamingChatResponseHandler handler,
            int attempt,
            Set<Integer> excluded,
            int callNo) {
        int maxAttempts = Math.max(1, Math.min(route.getFailoverMaxAttempts(), delegates.size()));
        if (attempt >= maxAttempts) {
            handler.onError(new AllEndpointsFailedException(attempt));
            return;
        }
        ModelRouteSelectionInput input = new ModelRouteSelectionInput(ctx, attempt, callNo, endpoints);
        int idx = selection.select(input, excluded);
        if (idx < 0 || idx >= delegates.size()) {
            handler.onError(new NoAvailableEndpointException("No streaming endpoint available"));
            return;
        }
        AtomicBoolean anyChunk = new AtomicBoolean(false);
        StreamingChatResponseHandler proxy = new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String partialResponse) {
                if (partialResponse != null && !partialResponse.isEmpty()) {
                    anyChunk.set(true);
                }
                handler.onPartialResponse(partialResponse);
            }

            @Override
            public void onPartialResponse(PartialResponse partialResponse, PartialResponseContext context) {
                anyChunk.set(true);
                handler.onPartialResponse(partialResponse, context);
            }

            @Override
            public void onPartialThinking(PartialThinking partialThinking) {
                anyChunk.set(true);
                handler.onPartialThinking(partialThinking);
            }

            @Override
            public void onPartialThinking(PartialThinking partialThinking, PartialThinkingContext context) {
                anyChunk.set(true);
                handler.onPartialThinking(partialThinking, context);
            }

            @Override
            public void onPartialToolCall(PartialToolCall partialToolCall) {
                anyChunk.set(true);
                handler.onPartialToolCall(partialToolCall);
            }

            @Override
            public void onPartialToolCall(PartialToolCall partialToolCall, PartialToolCallContext context) {
                anyChunk.set(true);
                handler.onPartialToolCall(partialToolCall, context);
            }

            @Override
            public void onCompleteToolCall(CompleteToolCall completeToolCall) {
                anyChunk.set(true);
                handler.onCompleteToolCall(completeToolCall);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                handler.onCompleteResponse(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                if (!anyChunk.get() && attempt + 1 < maxAttempts) {
                    excluded.add(idx);
                    startAttempt(chatRequest, handler, attempt + 1, excluded, callNo);
                } else {
                    handler.onError(error);
                }
            }
        };
        delegates.get(idx).doChat(chatRequest, proxy);
    }
}
