package com.astrsomn.starter.runtime.langchain.route;

import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelEndpoint;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelRouteSetting;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelSetting;
import com.astrsomn.api.runtime.common.langchain.extension.model.ModelProviderHandler;
import com.astrsomn.common.utils.StringUtils;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ModelRouteCompositeFactory {

    private final EndpointSelectionStrategyResolver selectionResolver;
    private final ResilienceDecorationStrategyResolver resilienceResolver;

    public boolean shouldWrap(AstroChatParam<?> param) {
        ModelSetting ms = param.getModelSetting();
        if (ms == null) {
            return false;
        }
        ModelRouteSetting route = ms.getModelRouteSetting();
        if (route == null || !route.isEnabled()) {
            return false;
        }
        List<ModelEndpoint> eps = ModelSettingRoutingSupport.resolveEndpoints(route, ms);
        if (eps.isEmpty()) {
            return false;
        }
        String rid = route.getResilienceStrategyId();
        boolean resilienceOn = StringUtils.isNotBlank(rid) && !"noop".equalsIgnoreCase(rid.trim());
        return eps.size() >= 2 || resilienceOn;
    }

    public <T> T buildComposite(ModelProviderHandler handler, Class<T> modelClass, AstroChatParam<?> param) {
        ModelSetting originalMs = param.getModelSetting();
        ModelRouteSetting route = originalMs.getModelRouteSetting();
        List<ModelEndpoint> endpoints = ModelSettingRoutingSupport.resolveEndpoints(route, originalMs);
        ModelRouteContext ctx = ModelRouteContext.from(param);
        EndpointSelectionStrategy selection = selectionResolver.resolve(route.getSelectionStrategyId());
        ResilienceDecorationStrategy resilience = resilienceResolver.resolve(route.getResilienceStrategyId());
        List<ChatModelListener> outerListeners =
                param.getChatModelListeners() == null ? List.of() : List.copyOf(param.getChatModelListeners());

        ModelSetting baseSnapshot = ModelSettingRoutingSupport.copyModelSetting(originalMs);

        if (StreamingChatModel.class.isAssignableFrom(modelClass)) {
            List<StreamingChatModel> delegates = new ArrayList<>();
            try {
                for (ModelEndpoint ep : endpoints) {
                    ModelSetting ms = ModelSettingRoutingSupport.copyModelSetting(baseSnapshot);
                    ModelSettingRoutingSupport.applyEndpoint(ms, ep);
                    param.setModelSetting(ms);
                    param.getModelSetting().setModelRouteSetting(ModelRouteSetting.disabled());
                    param.setChatModelListeners(List.of());
                    delegates.add((StreamingChatModel) handler.createModel(StreamingChatModel.class, param));
                }
            } finally {
                param.setModelSetting(originalMs);
                param.setChatModelListeners(outerListeners);
            }
            return modelClass.cast(new CompositeStreamingChatModel(
                    delegates, endpoints, route, ctx, selection, outerListeners));
        }

        if (ChatModel.class.isAssignableFrom(modelClass)) {
            List<ChatModel> delegates = new ArrayList<>();
            try {
                for (ModelEndpoint ep : endpoints) {
                    ModelSetting ms = ModelSettingRoutingSupport.copyModelSetting(baseSnapshot);
                    ModelSettingRoutingSupport.applyEndpoint(ms, ep);
                    param.setModelSetting(ms);
                    param.getModelSetting().setModelRouteSetting(ModelRouteSetting.disabled());
                    param.setChatModelListeners(List.of());
                    delegates.add((ChatModel) handler.createModel(ChatModel.class, param));
                }
            } finally {
                param.setModelSetting(originalMs);
                param.setChatModelListeners(outerListeners);
            }
            return modelClass.cast(new CompositeChatModel(
                    delegates, endpoints, route, ctx, selection, resilience, outerListeners));
        }

        ModelEndpoint first = endpoints.get(0);
        try {
            ModelSetting ms = ModelSettingRoutingSupport.copyModelSetting(baseSnapshot);
            ModelSettingRoutingSupport.applyEndpoint(ms, first);
            param.setModelSetting(ms);
            param.getModelSetting().setModelRouteSetting(ModelRouteSetting.disabled());
            param.setChatModelListeners(List.of());
            return handler.createModel(modelClass, param);
        } finally {
            param.setModelSetting(originalMs);
            param.setChatModelListeners(outerListeners);
        }
    }
}
