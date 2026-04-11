package org.astrsomn.vector.chroma.service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecDoc;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecStore;
import org.astrsomn.core.common.util.StringUtils;

public final class ChromaVecStoreHandler extends AbstractVecStore {

    private final ChromaVecSourceHandler chromaSource;
    private volatile EmbeddingStore<TextSegment> embeddingStoreCache;

    public ChromaVecStoreHandler(ChromaVecSourceHandler source, AiVecStoreEntity entity) {
        super(source, entity);
        this.chromaSource = source;
    }

    ChromaVecSourceHandler chromaSource() {
        return chromaSource;
    }

    @Override
    public void createCollection() {
        String name = collectionNameRequired();
        if (exists()) {
            return;
        }
        getEmbeddingStore(); // Chroma 会自动创建集合
    }

    @Override
    public void dropCollection() {
        try {
            // 创建一个临时的 embedding store 来删除集合
            ChromaEmbeddingStore.builder()
                    .baseUrl(chromaSource.baseUrl())

                    .collectionName(collectionNameRequired())
                    .build()
                    ;
        } finally {
            embeddingStoreCache = null;
        }
    }

    @Override
    public boolean exists() {
        try {
            // 创建一个临时的 embedding store 来检查集合是否存在
            ChromaEmbeddingStore embeddingStore = ChromaEmbeddingStore.builder()
                    .baseUrl(chromaSource.baseUrl())

                    .collectionName(collectionNameRequired())
                    .build();
            // 尝试获取集合信息，如果不存在会抛出异常

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long count() {
        return 1L;
    }

    @Override
    public EmbeddingStore<TextSegment> getEmbeddingStore() {
        if (embeddingStoreCache == null) {
            synchronized (this) {
                if (embeddingStoreCache == null) {
                    embeddingStoreCache = ChromaEmbeddingStore.builder()
                            .baseUrl(chromaSource.baseUrl())

                            .collectionName(collectionNameRequired())
                            .build();
                }
            }
        }
        return embeddingStoreCache;
    }

    @Override
    public AbstractVecDoc bindDoc(AiVecDocEntity doc) {
        return new ChromaVecDocHandler(this, doc);
    }

    private String collectionNameRequired() {
        String n = getEntity().getCollectionName();
        if (StringUtils.isBlank(n)) {
            throw new IllegalStateException("AiVecStoreEntity.collectionName is required");
        }
        return n.trim();
    }
}