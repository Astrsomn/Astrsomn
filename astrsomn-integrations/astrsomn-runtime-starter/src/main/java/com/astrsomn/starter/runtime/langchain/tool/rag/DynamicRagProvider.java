package com.astrsomn.starter.runtime.langchain.tool.rag;


import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.RagSetting;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DynamicRagProvider {

    private final VectorStoreRegistry vectorStoreRegistry;

    public ContentRetriever createRetriever(AstroChatParam param) {
        EmbeddingStore<TextSegment> embeddingStore = vectorStoreRegistry.getStore(param);
        EmbeddingModel embeddingModel = vectorStoreRegistry.getEmbeddingModel(param);
        RagSetting rag = param.getRagSetting() != null ? param.getRagSetting() : new RagSetting();

        int maxResults = rag.getMaxResults() != null && rag.getMaxResults() > 0
                ? rag.getMaxResults() : 5;
        double minScore = rag.getMinScore() != null && rag.getMinScore() > 0
                ? rag.getMinScore() : 0.0;

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
    }
}
