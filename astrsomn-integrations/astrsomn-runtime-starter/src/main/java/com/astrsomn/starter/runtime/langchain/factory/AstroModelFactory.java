package com.astrsomn.starter.runtime.langchain.factory;


import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.extension.model.ModelProviderHandler;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.langchain.exception.ModelProviderNotFoundException;
import com.astrsomn.starter.runtime.langchain.route.ModelRouteCompositeFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AstroModelFactory {

    private final ModelRouteCompositeFactory modelRouteCompositeFactory;

    // 存储 Provider 编码与处理器的映射
    private final Map<String, ModelProviderHandler> handlerMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void initSpi() {
        initSpiHandlers();
    }

    
    private void initSpiHandlers() {
        log.info("[Astro] Initializing handlers via SPI...");
        ServiceLoader<ModelProviderHandler> loader = ServiceLoader.load(ModelProviderHandler.class);

        for (ModelProviderHandler handler : loader) {
            String extensionCode = handler.getProvider().getCode();
            if (handlerMap.containsKey(extensionCode)) {
                log.warn("[Astro] Duplicate provider detected and skipped: {}", extensionCode);
                continue;
            }
            handlerMap.put(extensionCode, handler);
            log.info("[Astro] Loaded SPI Provider: {}", extensionCode);
        }
    }

    public <T> T createModel(AstroChatParam<?> param, Class<T> modelClass) {
        String modelProvider = param.getModelSetting().getExtensionCode();
        ModelProviderHandler handler = handlerMap.get(modelProvider);
        if (handler == null) {
            throw new ModelProviderNotFoundException(modelProvider);
        }
        if (modelRouteCompositeFactory.shouldWrap(param)) {
            return modelRouteCompositeFactory.buildComposite(handler, modelClass, param);
        }
        return handler.createModel(modelClass, param);
    }

    
    public Optional<ModelProviderHandler> getHandler(String extensionCode) {
        if (StringUtils.isEmpty(extensionCode)) {
            return Optional.empty();
        }
        return Optional.ofNullable(handlerMap.get(extensionCode.trim()));
    }

    // TODO 安装模型拓展插件
    public void registerHandler(ModelProviderHandler handler) {
        if (handler != null && handler.getProvider() != null) {
            handlerMap.put(handler.getProvider().getCode(), handler);
        }
    }

    // TODO 卸载模型拓展插件
    public void unregisterHandler(AiModelEnum.ProviderEnum provider) {
        if (provider != null) {
            handlerMap.remove(provider.getCode());
        }
    }
}
