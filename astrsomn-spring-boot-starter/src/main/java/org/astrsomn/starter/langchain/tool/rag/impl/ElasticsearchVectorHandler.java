package org.astrsomn.starter.langchain.tool.rag.impl;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.astrsomn.core.common.constant.VectorStoreType;
import org.astrsomn.core.common.langchain.buildParam.setting.RagSetting;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchVectorHandler implements VectorStoreHandler{
    @Override
    public boolean supports(VectorStoreType type) {
        return VectorStoreType.ELASTICSEARCH==type;
    }

    @Override
    public EmbeddingStore<TextSegment> getStore(RagSetting config) {
//        return ElasticsearchEmbeddingStore.builder()
//                .serverUrl(config.getUrl())
//                .apiKey(config.getApiKey())
//                .indexName(config.getNamespace())
//                .build();
        return null;
    }
}
