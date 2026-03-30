package org.astrsomn.starter.langchain.factory.core;


import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.mapper.AiAccountMapper;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.langchain.quota.ModelQuotaManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AiModelFactory {

    private final AiModelMapper aiModelMapper;
    private final AiAccountMapper aiAccountMapper;
    private final ModelQuotaManager quotaManager;
    private final AstrsomnProperties astrsomnProperties;

    // Spring 会自动注入所有实现 ModelProviderHandler 接口的 Bean
    private final List<ModelProviderHandler> handlers;

    public <T> T getModel(AstroChatParam<?> param, Class<T> modelClass) {
        AiModelEntity modelEntity = resolveModelEntity(param);
        AiAccountEntity accountEntity = resolveAccountEntity(modelEntity);
        AiModelEnum.ProviderEnum provider = AiModelEnum.ProviderEnum.fromCode(modelEntity.getProvider());

        return handlers.stream()
                .filter(h -> h.getProvider() == provider)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("不支持的厂商: " + modelEntity.getProvider()))
                .createModel(modelClass, modelEntity, accountEntity, param);
    }

    private AiAccountEntity resolveAccountEntity(AiModelEntity modelEntity) {
        return null;
    }

    private AiModelEntity resolveModelEntity(AstroChatParam<?> param) {
        return null;
    }



}
