package org.astrsomn.core.common.langchain.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;

import java.util.Optional;

/**
 * 由向量子模块实现：根据 {@link AstroChatParam}（含 {@code RagSetting}）查库并返回已构建的 {@link EmbeddingStore}。
 * Starter 仅注入本接口并委托，不直接访问 {@code AI_VEC_*} 表。
 */
public interface RagEmbeddingStoreResolver {

    /**
     * 解析并返回用于 RAG 检索的向量存储；实现类负责按 knowledgeKeys / storeId 等查 {@code AiVecSource}、{@code AiVecStore} 并调用 {@link org.astrsomn.core.common.langchain.extension.VecStoreBackend}。
     */
    EmbeddingStore<TextSegment> resolveEmbeddingStore(AstroChatParam<?> param);

    /**
     * 当未显式配置 {@code RagSetting.embeddingModelKey} 时，可用集合上绑定的嵌入模型 key（如 {@code AiVecStoreEntity.modelKey}）。
     */
    default Optional<String> resolveEmbeddingModelKey(AstroChatParam<?> param) {
        return Optional.empty();
    }
}
