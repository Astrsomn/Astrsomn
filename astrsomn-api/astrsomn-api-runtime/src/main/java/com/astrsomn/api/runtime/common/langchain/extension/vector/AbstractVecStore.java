package com.astrsomn.api.runtime.common.langchain.extension.vector;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.api.runtime.common.entity.AiVecDocEntity;
import com.astrsomn.api.runtime.common.entity.AiVecStoreEntity;

@Getter
@AllArgsConstructor
public abstract class AbstractVecStore implements VecStore {

    private final AbstractVecSource source;
    private final AiVecStoreEntity entity;

    @Override
    public abstract void createCollection();

    @Override
    public abstract void dropCollection();

    @Override
    public abstract boolean exists();

    @Override
    public abstract long count();

    @Override
    public abstract EmbeddingStore<TextSegment> getEmbeddingStore();

    @Override
    public abstract AbstractVecDoc bindDoc(AiVecDocEntity doc);
}
