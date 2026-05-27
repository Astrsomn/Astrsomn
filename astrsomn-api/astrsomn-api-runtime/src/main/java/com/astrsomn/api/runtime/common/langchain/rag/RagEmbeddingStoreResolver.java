package com.astrsomn.api.runtime.common.langchain.rag;

import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;

public interface RagEmbeddingStoreResolver {

    EmbeddingStore<TextSegment> resolveEmbeddingStore(AstroChatParam<?> param);

    EmbeddingModel resolveEmbeddingModel(AstroChatParam<?> param);

}