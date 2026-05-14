package com.astrsomn.api.runtime.common.langchain.extension.vector;

import com.astrsomn.api.runtime.common.entity.AiVecDocEntity;
import com.astrsomn.api.runtime.common.entity.AiVecStoreEntity;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;


public interface VecStore {

    VecSource getSource();

    AiVecStoreEntity getEntity();


    void createCollection();


    void dropCollection();


    boolean exists();


    long count();


    EmbeddingStore<TextSegment> getEmbeddingStore();


    VecDoc bindDoc(AiVecDocEntity doc);
}