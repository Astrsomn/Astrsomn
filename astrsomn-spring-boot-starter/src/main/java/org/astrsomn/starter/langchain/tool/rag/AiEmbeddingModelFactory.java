package org.astrsomn.starter.langchain.tool.rag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ModelSetting;
import org.astrsomn.core.common.langchain.buildParam.setting.RagSetting;
import org.astrsomn.core.common.langchain.rag.RagEmbeddingStoreResolver;
import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.mapper.AiAccountMapper;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.langchain.factory.AstroModelFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 从库表解析嵌入模型，临时写入 {@link ModelSetting} 后委托 {@link AstroModelFactory} 与 {@link org.astrsomn.core.common.langchain.extension.ModelProviderHandler} 创建 {@link EmbeddingModel}。
 */
@Component
public class AiEmbeddingModelFactory {

    private final AiModelMapper aiModelMapper;
    private final AiAccountMapper aiAccountMapper;
    private final AstrsomnProperties astrsomnProperties;
    private final AstroModelFactory astroModelFactory;
    private final ObjectProvider<RagEmbeddingStoreResolver> ragEmbeddingStoreResolver;

    public AiEmbeddingModelFactory(
            AiModelMapper aiModelMapper,
            AiAccountMapper aiAccountMapper,
            AstrsomnProperties astrsomnProperties,
            AstroModelFactory astroModelFactory,
            ObjectProvider<RagEmbeddingStoreResolver> ragEmbeddingStoreResolver) {
        this.aiModelMapper = aiModelMapper;
        this.aiAccountMapper = aiAccountMapper;
        this.astrsomnProperties = astrsomnProperties;
        this.astroModelFactory = astroModelFactory;
        this.ragEmbeddingStoreResolver = ragEmbeddingStoreResolver;
    }

    public <T> EmbeddingModel getEmbeddingModel(AstroChatParam<T> param) {
        String modelKey = resolveEmbeddingModelKey(param);
        AiModelEntity modelEntity = aiModelMapper.selectOne(new LambdaQueryWrapper<AiModelEntity>()
                .eq(AiModelEntity::getModelKey, modelKey)
                .eq(AiModelEntity::getEnvCode, astrsomnProperties.getEnvCode()));
        if (modelEntity == null) {
            throw new IllegalStateException("未找到嵌入模型: " + modelKey);
        }

        AiAccountEntity account = resolveAccount(modelEntity);
        ModelSetting previous = param.getModelSetting();
        ModelSetting embeddingMs = new ModelSetting();
        embeddingMs.setProvider(modelEntity.getProvider());
        embeddingMs.setModelName(modelEntity.getModelName());
        embeddingMs.setApiUrl(modelEntity.getApiUrl());
        embeddingMs.setApiKey(account.getApiKey());

        param.setModelSetting(embeddingMs);
        try {
            return astroModelFactory.createModel(param, EmbeddingModel.class);
        } finally {
            param.setModelSetting(previous);
        }
    }

    private String resolveEmbeddingModelKey(AstroChatParam<?> param) {
        RagSetting rag = param.getRagSetting();
        if (rag != null && StringUtils.isNotBlank(rag.getEmbeddingModelKey())) {
            return rag.getEmbeddingModelKey().trim();
        }
        Optional<String> fromStore = Optional.ofNullable(ragEmbeddingStoreResolver.getIfAvailable())
                .flatMap(r -> r.resolveEmbeddingModelKey(param));
        if (fromStore.isPresent()) {
            return fromStore.get();
        }
        return param.getModelKey();
    }

    private AiAccountEntity resolveAccount(AiModelEntity modelEntity) {
        if (StringUtils.isBlank(modelEntity.getAccountKey())) {
            throw new IllegalStateException("模型未配置 accountKey: " + modelEntity.getModelKey());
        }
        AiAccountEntity account = aiAccountMapper.selectOne(new LambdaQueryWrapper<AiAccountEntity>()
                .eq(AiAccountEntity::getAccountKey, modelEntity.getAccountKey()));
        if (account == null || StringUtils.isBlank(account.getApiKey())) {
            throw new IllegalStateException("未找到账号或 API Key: " + modelEntity.getAccountKey());
        }
        return account;
    }
}
