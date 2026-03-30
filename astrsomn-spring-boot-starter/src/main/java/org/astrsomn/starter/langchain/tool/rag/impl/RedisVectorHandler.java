package org.astrsomn.starter.langchain.tool.rag.impl;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.astrsomn.core.common.constant.VectorStoreType;
import org.astrsomn.core.common.langchain.buildParam.setting.RagSetting;

public class RedisVectorHandler implements VectorStoreHandler{
    @Override
    public boolean supports(VectorStoreType type) {
        return  type == VectorStoreType.REDIS;
    }

    @Override
    public EmbeddingStore<TextSegment> getStore(RagSetting config) {
//        return RedisEmbeddingStore.builder()
//                .host(config.getHost())
//                .port(config.getPort())
//                .indexName(config.getNamespace())
//                .build();
        return null;
    }
}
