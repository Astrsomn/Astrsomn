package org.astrsomn.vector.qdrant.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.RagSetting;
import org.astrsomn.core.common.langchain.extension.VecStoreBackend;
import org.astrsomn.core.common.langchain.rag.RagEmbeddingStoreResolver;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.mapper.AiVecSourceMapper;
import org.astrsomn.core.mapper.AiVecStoreMapper;
import org.astrsomn.vector.qdrant.VecStoreBackendRegistry;

import java.util.List;
import java.util.Optional;

/**
 * 根据 {@code RagSetting.knowledgeKeys} 首项解析为 {@code AI_VEC_STORE.ID}，加载 Source/Store 并返回 {@link EmbeddingStore}。
 */
public class DefaultRagEmbeddingStoreResolver implements RagEmbeddingStoreResolver {

    private final AiVecSourceMapper aiVecSourceMapper;
    private final AiVecStoreMapper aiVecStoreMapper;
    private final VecStoreBackendRegistry vecStoreBackendRegistry;

    public DefaultRagEmbeddingStoreResolver(
            AiVecSourceMapper aiVecSourceMapper,
            AiVecStoreMapper aiVecStoreMapper,
            VecStoreBackendRegistry vecStoreBackendRegistry) {
        this.aiVecSourceMapper = aiVecSourceMapper;
        this.aiVecStoreMapper = aiVecStoreMapper;
        this.vecStoreBackendRegistry = vecStoreBackendRegistry;
    }

    @Override
    public EmbeddingStore<TextSegment> resolveEmbeddingStore(AstroChatParam<?> param) {
        AiVecStoreEntity store = loadStoreRequired(param);
        AiVecSourceEntity source = aiVecSourceMapper.selectById(store.getSourceId());
        if (source == null) {
            throw new IllegalStateException("未找到向量数据源: sourceId=" + store.getSourceId());
        }
        VecStoreBackend backend = vecStoreBackendRegistry.getRequired(source.getProvider());
        return backend.openEmbeddingStore(source, store);
    }

    @Override
    public Optional<String> resolveEmbeddingModelKey(AstroChatParam<?> param) {
        return findStore(param)
                .map(AiVecStoreEntity::getModelKey)
                .flatMap(mk -> Optional.ofNullable(StringUtils.trimToNull(mk)));
    }

    private AiVecStoreEntity loadStoreRequired(AstroChatParam<?> param) {
        return findStore(param).orElseThrow(
                () -> new IllegalStateException("RAG 未配置有效 knowledgeKeys（首项应为 AI_VEC_STORE.ID）或集合不存在"));
    }

    private Optional<AiVecStoreEntity> findStore(AstroChatParam<?> param) {
        RagSetting rag = param.getRagSetting();
        if (rag == null) {
            return Optional.empty();
        }
        List<String> keys = rag.getKnowledgeKeys();
        if (keys == null || keys.isEmpty()) {
            return Optional.empty();
        }
        String first = StringUtils.trimToNull(keys.get(0));
        if (first == null) {
            return Optional.empty();
        }
        long storeId;
        try {
            storeId = Long.parseLong(first);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        return Optional.ofNullable(aiVecStoreMapper.selectById(storeId));
    }
}
