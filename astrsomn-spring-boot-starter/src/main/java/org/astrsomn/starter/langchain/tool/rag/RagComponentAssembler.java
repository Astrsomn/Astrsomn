package org.astrsomn.starter.langchain.tool.rag;


import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.RagSetting;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RagComponentAssembler {

    private final AiEmbeddingModelFactory embeddingModelFactory;
    private final VectorStoreRegistry vectorStoreRegistry;

    public ContentRetriever createRetriever(AstroChatParam param) {
        EmbeddingModel embeddingModel = embeddingModelFactory.getEmbeddingModel(param);
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
