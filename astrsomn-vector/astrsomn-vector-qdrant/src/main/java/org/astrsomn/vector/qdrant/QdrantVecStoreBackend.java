package org.astrsomn.vector.qdrant;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;
import org.astrsomn.core.common.langchain.extension.VecStoreBackend;
import org.astrsomn.core.common.langchain.vector.AbstractVecSource;
import org.astrsomn.core.common.util.StringUtils;

/**
 * Qdrant 向量后端：集合生命周期走 Qdrant HTTP API；运行时检索/入库走 LangChain4j 。
 * <p>
 * 扁平 API 委托给 {@link AbstractVecSource} / {@link QdrantVecStore}，与分层抽象共用一套实现。
 */
public class QdrantVecStoreBackend implements VecStoreBackend {

    public static final String EXTENSION_KEY = "qdrant";

    @Override
    public String getExtensionKey() {
        return EXTENSION_KEY;
    }

    @Override
    public AbstractVecSource bindSource(AiVecSourceEntity source) {
        return new QdrantVecSource(source);
    }

    @Override
    public void testConnection(AiVecSourceEntity source) {
        bindSource(source).testConnection();
    }

    @Override
    public void createOrUpdateCollection(AiVecSourceEntity source, AiVecStoreEntity store) {
        validateStore(store);
        ((QdrantVecStore) bindSource(source).openStore(store)).createOrUpdatePhysicalCollection();
    }

    @Override
    public void deleteCollection(AiVecSourceEntity source, AiVecStoreEntity store) {
        if (store == null || StringUtils.isEmpty(store.getCollectionName())) {
            throw new IllegalArgumentException("collectionName is required");
        }
        ((QdrantVecStore) bindSource(source).openStore(store)).deletePhysicalCollection();
    }

    @Override
    public EmbeddingStore<TextSegment> openEmbeddingStore(AiVecSourceEntity source, AiVecStoreEntity store) {
        validateStore(store);
        return ((QdrantVecStore) bindSource(source).openStore(store)).openEmbeddingStore();
    }

    private static void validateStore(AiVecStoreEntity store) {
        if (store == null || StringUtils.isEmpty(store.getCollectionName())) {
            throw new IllegalArgumentException("AiVecStore.collectionName is required");
        }
    }

    static String mapDistance(String metric) {
        if (metric == null || metric.isBlank()) {
            return "Cosine";
        }
        return switch (metric.trim().toUpperCase()) {
            case "EUCLID", "L2", "EUCLIDEAN" -> "Euclid";
            case "DOT", "IP", "INNER_PRODUCT" -> "Dot";
            case "MANHATTAN" -> "Manhattan";
            default -> "Cosine";
        };
    }
}
