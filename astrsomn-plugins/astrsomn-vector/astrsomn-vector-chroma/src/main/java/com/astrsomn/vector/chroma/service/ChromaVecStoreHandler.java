package com.astrsomn.vector.chroma.service;

import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecDoc;
import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecStore;
import com.astrsomn.api.vector.entity.AiVecDocEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.vector.chroma.internal.ChromaCollectionAdmin;
import com.astrsomn.vector.chroma.internal.ChromaEmbeddingStores;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;

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
        getEmbeddingStore();
    }

    @Override
    public void dropCollection() {
        try {
            ChromaCollectionAdmin.deleteCollection(chromaSource.getEntity(), collectionNameRequired());
        } finally {
            embeddingStoreCache = null;
        }
    }

    @Override
    public boolean exists() {
        return ChromaCollectionAdmin.collectionExists(chromaSource.getEntity(), collectionNameRequired());
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
