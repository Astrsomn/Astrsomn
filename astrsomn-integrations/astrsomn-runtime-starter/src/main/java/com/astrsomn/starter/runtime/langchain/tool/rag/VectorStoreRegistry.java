package com.astrsomn.starter.runtime.langchain.tool.rag;

import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.rag.RagEmbeddingStoreResolver;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class VectorStoreRegistry {

    private final ObjectProvider<RagEmbeddingStoreResolver> ragEmbeddingStoreResolver;

    public VectorStoreRegistry(ObjectProvider<RagEmbeddingStoreResolver> ragEmbeddingStoreResolver) {
        this.ragEmbeddingStoreResolver = ragEmbeddingStoreResolver;
    }

    public EmbeddingStore<TextSegment> getStore(AstroChatParam<?> param) {
        RagEmbeddingStoreResolver resolver = ragEmbeddingStoreResolver.getIfAvailable();
        if (resolver == null) {
            throw new IllegalStateException(
                    "未注册 RagEmbeddingStoreResolver：请在应用中引入向量子模块（如 astrsomn-vector-qdrant）");
        }
        return resolver.resolveEmbeddingStore(param);
    }
}
