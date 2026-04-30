package com.astrsomn.core.common.langchain.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import com.astrsomn.core.common.langchain.buildParam.AstroChatParam;




public interface RagEmbeddingStoreResolver {


    EmbeddingStore<TextSegment> resolveEmbeddingStore(AstroChatParam<?> param);



}
