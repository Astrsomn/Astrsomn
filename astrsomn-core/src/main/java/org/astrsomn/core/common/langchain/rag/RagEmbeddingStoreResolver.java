package org.astrsomn.core.common.langchain.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;

import java.util.Optional;


public interface RagEmbeddingStoreResolver {


    EmbeddingStore<TextSegment> resolveEmbeddingStore(AstroChatParam<?> param);


    default Optional<String> resolveEmbeddingModelKey(AstroChatParam<?> param) {
        return Optional.empty();
    }
}
