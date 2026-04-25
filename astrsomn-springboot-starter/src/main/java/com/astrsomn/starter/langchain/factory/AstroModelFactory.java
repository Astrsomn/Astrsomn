package com.astrsomn.starter.langchain.factory;


import lombok.extern.slf4j.Slf4j;
import com.astrsomn.core.common.constant.AiModelEnum;
import com.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.core.common.langchain.extension.model.ModelProviderHandler;
import com.astrsomn.commn.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AstroModelFactory {

    // 存储 Provider 编码与处理器的映射
    private final Map<String, ModelProviderHandler> handlerMap = new ConcurrentHashMap<>();

    public AstroModelFactory() {
        initSpiHandlers();
    }

    /**
     * 完全通过 Java SPI 加载处理器
     */
    private void initSpiHandlers() {
        log.info("[Astro] Initializing handlers via SPI...");
        ServiceLoader<ModelProviderHandler> loader = ServiceLoader.load(ModelProviderHandler.class);

        for (ModelProviderHandler handler : loader) {
            String providerCode = handler.getProvider().getCode();
            if (handlerMap.containsKey(providerCode)) {
                log.warn("[Astro] Duplicate provider detected and skipped: {}", providerCode);
                continue;
            }
            handlerMap.put(providerCode, handler);
            log.info("[Astro] Loaded SPI Provider: {}", providerCode);
        }
    }

    public <T> T createModel(AstroChatParam<?> param, Class<T> modelClass) {
        String modelProvider = param.getModelSetting().getProvider();
        ModelProviderHandler handler = handlerMap.get(modelProvider);
        if (handler == null) {
            throw new RuntimeException("Unsupported provider: " + modelProvider);
        }
        return handler.createModel(modelClass, param);
    }

    /**
     * 按厂商 code（与 {@link AiModelEnum.ProviderEnum#getCode()} 一致）解析 SPI 注册的处理器。
     */
    public Optional<ModelProviderHandler> getHandler(String providerCode) {
        if (StringUtils.isEmpty(providerCode)) {
            return Optional.empty();
        }
        return Optional.ofNullable(handlerMap.get(providerCode.trim()));
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
