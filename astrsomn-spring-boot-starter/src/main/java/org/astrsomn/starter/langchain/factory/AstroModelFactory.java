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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AstroModelFactory {

    private final AiModelMapper aiModelMapper;
    private final AiAccountMapper aiAccountMapper;
    private final ModelQuotaManager quotaManager;
    private final AstrsomnProperties astrsomnProperties;
    private final Map<AiModelEnum.ProviderEnum, ModelProviderHandler> handlerMap = new ConcurrentHashMap<>();
    // Spring 会自动注入所有实现 ModelProviderHandler 接口的 Bean
    private final List<ModelProviderHandler> handlers;

    private static final List<ModelProviderHandler> CACHED_HANDLERS = new ArrayList<>();

    static {
        // 利用 Java SPI 加载所有 classpath 下的实现
        ServiceLoader<ModelProviderHandler> loader = ServiceLoader.load(ModelProviderHandler.class);
        for (ModelProviderHandler handler : loader) {
            log.info("====>  [Astrsomn] Loaded AI Provider: {}", handler.getProvider());
            CACHED_HANDLERS.add(handler);
        }
    }


    public <T> T createModel(AstroChatParam<?> param, Class<T> modelClass) {
        AiModelEntity modelEntity = resolveModelEntity(param);
        AiAccountEntity accountEntity = resolveAccountEntity(modelEntity);
        AiModelEnum.ProviderEnum provider = AiModelEnum.ProviderEnum.fromCode(modelEntity.getProvider());

        return handlers.stream()
                .filter(h -> h.getProvider() == provider)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("不支持的厂商: " + modelEntity.getProvider()))
                .createModel(modelClass, param);
    }

    private AiAccountEntity resolveAccountEntity(AiModelEntity modelEntity) {
        return aiAccountMapper.selectOne(new LambdaQueryWrapper<AiAccountEntity>()
                .eq(AiAccountEntity::getEnvCode, modelEntity.getEnvCode())
                .eq(AiAccountEntity::getAccountKey, modelEntity.getAccountKey()));
    }

    private AiModelEntity resolveModelEntity(AstroChatParam<?> param) {
        AiModelEntity aiModelEntity = aiModelMapper.selectOne(new LambdaQueryWrapper<AiModelEntity>()
                .eq(AiModelEntity::getEnvCode, astrsomnProperties.getEnvCode())
                .eq(AiModelEntity::getModelKey, param.getModelKey()));
        return aiModelEntity;
    }

    // 动态注册：供 PluginManager 调用
    public void registerHandler(ModelProviderHandler handler) {
        handlerMap.put(handler.getProvider(), handler);
    }



}
