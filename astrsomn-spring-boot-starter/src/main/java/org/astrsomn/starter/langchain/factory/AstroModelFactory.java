package org.astrsomn.starter.langchain.factory;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.extension.ModelProviderHandler;
import org.astrsomn.core.mapper.AiAccountMapper;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.langchain.quota.ModelQuotaManager;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
     * 仍然保留动态注册接口，方便 PluginManager 在运行时手动注入
     */
    public void registerHandler(ModelProviderHandler handler) {
        if (handler != null && handler.getProvider() != null) {
            handlerMap.put(handler.getProvider().getCode(), handler);
        }
    }

    public void unregisterHandler(AiModelEnum.ProviderEnum provider) {
        if (provider != null) {
            handlerMap.remove(provider.getCode());
        }
    }
}
