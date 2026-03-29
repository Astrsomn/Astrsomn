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
    // 这里可以注入一个获取 EmbeddingStore 的 Service
    private final VectorStoreRegistry vectorStoreRegistry;

    public ContentRetriever createRetriever(AstroChatParam param) {
        // 1. 获取对应的 Embedding 模型（例如 OpenAI, DashScope 等）
        EmbeddingModel embeddingModel = embeddingModelFactory.getEmbeddingModel(param);

        // 2. 获取对应的向量数据库实例（例如针对特定租户或业务的索引）
        EmbeddingStore<TextSegment> embeddingStore = vectorStoreRegistry.getStore(param);

        // 3. 构建检索器
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(param.getRagSetting().getMaxResults()) // 每次检索多少条记录
                .minScore(param.getRagSetting().getMinScore())     // 最低相关性分数阈值
                .build();
    }


}
