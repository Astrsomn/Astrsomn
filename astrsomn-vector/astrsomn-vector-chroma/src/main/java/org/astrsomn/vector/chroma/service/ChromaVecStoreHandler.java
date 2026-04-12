package org.astrsomn.vector.chroma.service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecDoc;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecStore;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.vector.chroma.internal.ChromaEmbeddingStores;

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
        if (exists()) {
            return;
        }
        getEmbeddingStore(); // Chroma 会自动创建集合
    }

    @Override
    public void dropCollection() {
        try {
            ChromaEmbeddingStores.buildForCollection(chromaSource.getEntity(), collectionNameRequired());
        } finally {
            embeddingStoreCache = null;
        }
    }

    @Override
    public boolean exists() {
        try {
            ChromaEmbeddingStores.buildForCollection(chromaSource.getEntity(), collectionNameRequired());
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
                    embeddingStoreCache =
                            ChromaEmbeddingStores.buildForCollection(chromaSource.getEntity(), collectionNameRequired());
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
