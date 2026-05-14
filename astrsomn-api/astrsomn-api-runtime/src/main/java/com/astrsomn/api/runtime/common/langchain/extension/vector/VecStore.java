package com.astrsomn.api.runtime.common.langchain.extension.vector;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import com.astrsomn.api.runtime.common.entity.AiVecDocEntity;
import com.astrsomn.api.runtime.common.entity.AiVecStoreEntity;


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