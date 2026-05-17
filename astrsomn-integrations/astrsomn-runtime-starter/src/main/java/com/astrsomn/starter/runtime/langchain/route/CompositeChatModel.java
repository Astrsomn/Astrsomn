package com.astrsomn.starter.runtime.langchain.route;

import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelEndpoint;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelRouteSetting;
import com.astrsomn.starter.runtime.langchain.exception.AllEndpointsFailedException;
import com.astrsomn.starter.runtime.langchain.exception.NoAvailableEndpointException;
import dev.langchain4j.model.ModelProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 多 {@link ChatModel} 组合：同步路径支持 {@link ResilienceDecorationStrategy}；跨节点故障转移在组合层完成。
 */
public class CompositeChatModel implements ChatModel {

    private final List<ChatModel> delegates;
    private final List<ModelEndpoint> endpoints;
    private final ModelRouteSetting route;
    private final ModelRouteContext ctx;
    private final EndpointSelectionStrategy selection;
    private final ResilienceDecorationStrategy resilience;
    private final List<ChatModelListener> outerListeners;
    private final AtomicInteger monotonicCall = new AtomicInteger(0);

    public CompositeChatModel(
            List<ChatModel> delegates,
            List<ModelEndpoint> endpoints,
            ModelRouteSetting route,
            ModelRouteContext ctx,
            EndpointSelectionStrategy selection,
            ResilienceDecorationStrategy resilience,
            List<ChatModelListener> outerListeners) {
        this.delegates = List.copyOf(delegates);
        this.endpoints = List.copyOf(endpoints);
        this.route = route;
        this.ctx = ctx;
        this.selection = selection;
        this.resilience = resilience;
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
    public ChatResponse doChat(ChatRequest chatRequest) {
        int maxAttempts = Math.max(1, Math.min(route.getFailoverMaxAttempts(), delegates.size()));
        Set<Integer> excluded = new HashSet<>();
        RuntimeException last = null;
        int callNo = monotonicCall.getAndIncrement();
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            ModelRouteSelectionInput input = new ModelRouteSelectionInput(ctx, attempt, callNo, endpoints);
            int idx = selection.select(input, excluded);
            if (idx < 0 || idx >= delegates.size()) {
                break;
            }
            int chosen = idx;
            try {
                Supplier<ChatResponse> call = () -> delegates.get(chosen).doChat(chatRequest);
                return resilience.decorate(call, ctx, chosen, route);
            } catch (RuntimeException e) {
                last = e;
                excluded.add(chosen);
            } catch (Exception e) {
                last = new RuntimeException(e);
                excluded.add(chosen);
            }
        }
        if (last != null) {
            throw new AllEndpointsFailedException(maxAttempts, last);
        }
        throw new NoAvailableEndpointException("No chat delegate available for routing");
    }
}
