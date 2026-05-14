package com.astrsomn.api.runtime.common.langchain.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;




public interface RagEmbeddingStoreResolver {


    EmbeddingStore<TextSegment> resolveEmbeddingStore(AstroChatParam<?> param);



}