package com.astrsomn.starter.runtime.langchain.tool.rag;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.entity.AiAccountEntity;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelSetting;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.RagSetting;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ToolSetting;
import com.astrsomn.api.runtime.common.langchain.rag.RagEmbeddingStoreResolver;
import com.astrsomn.api.vector.constant.AiVecSourceEnum;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.common.utils.CryptoUtil;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.langchain.exception.VectorStoreException;
import com.astrsomn.starter.runtime.langchain.factory.AstroModelFactory;
import com.astrsomn.starter.runtime.langchain.runtime.chain.RuntimeChatParamMergeSupport;
import com.astrsomn.starter.runtime.mapper.AstAiAccountMapper;
import com.astrsomn.starter.runtime.mapper.AstAiInstanceMapper;
import com.astrsomn.starter.runtime.mapper.AstAiModelMapper;
import com.astrsomn.starter.runtime.vector.AstroVecSourceFactory;
import com.astrsomn.starter.runtime.vector.mapper.AstAiVecSourceMapper;
import com.astrsomn.starter.runtime.vector.mapper.AstAiVecStoreMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultRagEmbeddingStoreResolver implements RagEmbeddingStoreResolver {

    private final AstAiVecStoreMapper aiVecStoreMapper;
    private final AstAiVecSourceMapper aiVecSourceMapper;
    private final AstroVecSourceFactory astroVecSourceFactory;
    private final AstAiInstanceMapper aiInstanceMapper;
    private final AstAiModelMapper aiModelMapper;
    private final AstAiAccountMapper astAiAccountMapper;
    private final AstroModelFactory astroModelFactory;

    @Override
    public EmbeddingStore<TextSegment> resolveEmbeddingStore(AstroChatParam<?> param) {
        AiVecStoreEntity store = resolveFirstMatchingStore(param);
        return openEmbeddingStore(store);
    }

    @Override
    public EmbeddingModel resolveEmbeddingModel(AstroChatParam<?> param) {
        AiVecStoreEntity store = resolveFirstMatchingStore(param);

        AiInstanceEntity instance = aiInstanceMapper.selectOne(
                new LambdaQueryWrapper<AiInstanceEntity>()
                        .eq(AiInstanceEntity::getInstanceKey, store.getInstanceKey().trim())
                        .eq(AiInstanceEntity::getDeleted, false)
                        .last("LIMIT 1"));
        if (Objects.isNull(instance)) {
            throw new VectorStoreException("Embedding instance not found: " + store.getInstanceKey());
        }

        String modelKey = StringUtils.isNotBlank(store.getModelKey())
                ? store.getModelKey().trim()
                : StringUtils.trimToNull(instance.getModelKey());
        if (Objects.isNull(modelKey)) {
            throw new VectorStoreException("No model key configured for embedding store id=" + store.getId());
        }

        AiModelEntity model = aiModelMapper.selectOne(
                new LambdaQueryWrapper<AiModelEntity>()
                        .eq(AiModelEntity::getModelKey, modelKey)
                        .eq(AiModelEntity::getDeleted, false)
                        .last("LIMIT 1"));
        if (Objects.isNull(model)) {
            throw new VectorStoreException("Model not found: " + modelKey);
        }

        String modelType = StringUtils.trimToNull(model.getModelType());
        if (!AiModelEnum.ModelTypeEnum.EMBEDDING_MODEL.getCode().equalsIgnoreCase(modelType)) {
            throw new VectorStoreException("Model type must be embedding, got: " + modelType);
        }

        AstroChatParam<EmbeddingModel> embedParam =
                AstroChatParam.<EmbeddingModel>builder().serviceClass(EmbeddingModel.class).build();
        embedParam.setInstanceKey(instance.getInstanceKey());
        embedParam.setModelKey(modelKey);
        embedParam.setModelSetting(new ModelSetting());
        RuntimeChatParamMergeSupport.mergeChatSettingFromInstance(embedParam.getChatSetting(), instance);
        RuntimeChatParamMergeSupport.mergeModelSettingFromModel(embedParam.getModelSetting(), model);

        String extensionCode = StringUtils.trimToNull(model.getExtensionCode());
        String modelName = StringUtils.trimToNull(model.getModelName());
        log.info("RAG embedding model: instanceKey={}, modelKey={}, extensionCode={}, modelName={}",
                instance.getInstanceKey(), modelKey, extensionCode, modelName);

        String accountKey = StringUtils.trimToNull(instance.getAccountKey());
        if (Objects.nonNull(accountKey)) {
            AiAccountEntity account = astAiAccountMapper.selectOne(
                    new LambdaQueryWrapper<AiAccountEntity>()
                            .eq(AiAccountEntity::getAccountKey, accountKey)
                            .eq(AiAccountEntity::getDeleted, false)
                            .last("LIMIT 1"));
            if (Objects.nonNull(account)) {
                if (account.getApiKey() != null) {
                    account.setApiKey(CryptoUtil.decrypt(account.getApiKey()));
                }
                if (account.getApiSecret() != null) {
                    account.setApiSecret(CryptoUtil.decrypt(account.getApiSecret()));
                }
                RuntimeChatParamMergeSupport.mergeModelSettingFromAccount(embedParam.getModelSetting(), account);
            }
        }

        EmbeddingModel embeddingModel = astroModelFactory.createModel(embedParam, EmbeddingModel.class);
        log.info("RAG embedding model created: class={}, extensionCode={}, modelName={}",
                embeddingModel.getClass().getSimpleName(), extensionCode, modelName);
        return embeddingModel;
    }

    private AiVecStoreEntity resolveFirstMatchingStore(AstroChatParam<?> param) {
        List<String> ragKeys = getRagKeys(param);
        if (ragKeys == null || ragKeys.isEmpty()) {
            throw new VectorStoreException(
                    "No knowledge keys configured. Set toolSetting.ragKeys or ragSetting.knowledgeKeys on the agent.");
        }
        log.info("Resolving RAG store: knowledgeKeys={}", ragKeys);
        for (String key : ragKeys) {
            AiVecStoreEntity store = findStoreByKey(key);
            if (store != null) {
                log.info("Resolved RAG store: key={}, storeId={}, collectionName={}, sourceId={}",
                        key, store.getId(), store.getCollectionName(), store.getSourceId());
                return store;
            }
            log.warn("No store found for knowledge key: {}", key);
        }
        throw new VectorStoreException("No vector store found for knowledge keys: " + ragKeys);
    }

    private List<String> getRagKeys(AstroChatParam<?> param) {
        ToolSetting toolSetting = param.getToolSetting();
        if (toolSetting != null && toolSetting.getRagKeys() != null && !toolSetting.getRagKeys().isEmpty()) {
            return toolSetting.getRagKeys();
        }
        RagSetting ragSetting = param.getRagSetting();
        if (ragSetting != null && ragSetting.getKnowledgeKeys() != null && !ragSetting.getKnowledgeKeys().isEmpty()) {
            return ragSetting.getKnowledgeKeys();
        }
        return null;
    }

    private AiVecStoreEntity findStoreByKey(String key) {
        String trimmed = StringUtils.trimToNull(key);
        if (trimmed == null) {
            return null;
        }
        AiVecStoreEntity store = aiVecStoreMapper.selectOne(
                new LambdaQueryWrapper<AiVecStoreEntity>()
                        .eq(AiVecStoreEntity::getCollectionName, trimmed)
                        .eq(AiVecStoreEntity::getDeleted, false)
                        .last("LIMIT 1"));
        if (store != null) {
            return store;
        }
        try {
            Long id = Long.parseLong(trimmed);
            return aiVecStoreMapper.selectById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private EmbeddingStore<TextSegment> openEmbeddingStore(AiVecStoreEntity store) {
        Long sourceId = store.getSourceId();
        if (Objects.isNull(sourceId)) {
            throw new VectorStoreException("Store " + store.getId() + " has no sourceId configured");
        }
        var vecSource = astroVecSourceFactory.tryGetActiveSource(sourceId).orElse(null);
        if (Objects.isNull(vecSource)) {
            log.warn("RAG: source not active, attempting auto-register. sourceId={}", sourceId);
            AiVecSourceEntity sourceEntity = aiVecSourceMapper.selectById(sourceId);
            if (Objects.isNull(sourceEntity)) {
                throw new VectorStoreException("Vector source not found: " + sourceId);
            }
            log.info("RAG: source status={}, extensionCode={}", sourceEntity.getStatus(), sourceEntity.getExtensionCode());
            if (!AiVecSourceEnum.StatusEnum.ENABLED.getCode().equals(sourceEntity.getStatus())) {
                log.warn("RAG: source is {}, auto-enabling. sourceId={}", sourceEntity.getStatus(), sourceId);
                sourceEntity.setStatus(AiVecSourceEnum.StatusEnum.ENABLED.getCode());
                aiVecSourceMapper.updateById(sourceEntity);
            }
            astroVecSourceFactory.registerOrRefresh(sourceEntity);
            vecSource = astroVecSourceFactory.tryGetActiveSource(sourceId)
                    .orElseThrow(() -> new VectorStoreException("Vector source not ready after register: " + sourceId));
            log.info("RAG: source registered successfully. sourceId={}", sourceId);
        }
        EmbeddingStore<TextSegment> result = vecSource.openStore(store).getEmbeddingStore();
        log.info("RAG embedding store opened: storeId={}, collectionName={}, storeClass={}",
                store.getId(), store.getCollectionName(), result.getClass().getSimpleName());
        return result;
    }
}
