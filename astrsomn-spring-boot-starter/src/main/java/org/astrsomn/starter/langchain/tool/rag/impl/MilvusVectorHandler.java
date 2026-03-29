package org.astrsomn.starter.langchain.tool.rag.impl;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.astrsomn.core.common.constant.VectorStoreType;
import org.astrsomn.core.common.langchain.buildParam.setting.RagSetting;

public class MilvusVectorHandler implements VectorStoreHandler{
    @Override
    public boolean supports(VectorStoreType type) {
        return VectorStoreType.MILVUS == type;
    }

    @Override
    public EmbeddingStore<TextSegment> getStore(RagSetting config) {
//        return MilvusEmbeddingStore.builder()
//                .uri(config.getUrl())
//                .collectionName(config.getNamespace())
//                .dimension(config.getDimension())
//                .build();
        return null;
    }
}
