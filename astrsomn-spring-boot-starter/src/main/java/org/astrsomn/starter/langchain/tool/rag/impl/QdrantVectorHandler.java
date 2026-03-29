package org.astrsomn.starter.langchain.tool.rag.impl;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.astrsomn.core.common.constant.VectorStoreType;
import org.astrsomn.core.common.entity.RagKnowledgeEntity;
import org.astrsomn.core.common.langchain.buildParam.setting.RagSetting;
import org.springframework.stereotype.Component;

@Component
public class QdrantVectorHandler implements VectorStoreHandler{
    @Override
    public boolean supports(VectorStoreType type) {
        return type == VectorStoreType.QDRANT;
    }

    @Override
    public EmbeddingStore<TextSegment> getStore(RagSetting config) {
        return QdrantEmbeddingStore.builder()
                .host(config.getHost())
                .collectionName(config.getNamespace())
                .build();
    }
}
