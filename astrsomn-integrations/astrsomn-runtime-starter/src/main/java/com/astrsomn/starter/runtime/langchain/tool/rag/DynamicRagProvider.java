package com.astrsomn.starter.runtime.langchain.tool.rag;


import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.RagSetting;
import com.astrsomn.starter.runtime.langchain.factory.AstroModelFactory;
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


    private final AstroModelFactory astroModelFactory;
    private final VectorStoreRegistry vectorStoreRegistry;

    public ContentRetriever createRetriever(AstroChatParam param) {
        EmbeddingModel embeddingModel = astroModelFactory.createModel(param, EmbeddingModel.class);
        EmbeddingStore<TextSegment> embeddingStore = vectorStoreRegistry.getStore(param);
        RagSetting rag = param.getRagSetting() != null ? param.getRagSetting() : new RagSetting();
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(rag.getMaxResults())
                .minScore(rag.getMinScore())
                .build();
    }


}
