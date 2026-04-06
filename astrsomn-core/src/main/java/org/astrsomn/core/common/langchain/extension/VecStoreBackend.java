package org.astrsomn.core.common.langchain.extension;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;
import org.astrsomn.core.common.langchain.vector.AbstractVecSource;

/**
 * 向量库后端契约：与 {@link org.astrsomn.core.common.langchain.extension.ModelProviderHandler} 对称，
 * 由各 {@code astrsomn-vector-*} 模块实现并通过 SPI 注册。
 */
public interface VecStoreBackend {

    /**
     * 与 {@link org.astrsomn.core.common.entity.AiVecSourceEntity#getProvider()}、
     * {@link org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor#getExtensionKey()} 一致。
     */
    String getExtensionKey();

    /**
     * 将数据源实体绑定为可逐级下探的抽象句柄（Source → Store → Doc → Segment），用于统一获取连接与领域操作。
     */
    AbstractVecSource bindSource(AiVecSourceEntity source);

    /**
     * 探测连接是否可用（不保证集合已存在）。
     */
    void testConnection(AiVecSourceEntity source);

    /**
     * 创建或更新物理集合/索引（维度、距离度量等与 {@link AiVecStoreEntity} 对齐）。
     */
    void createOrUpdateCollection(AiVecSourceEntity source, AiVecStoreEntity store);

    /**
     * 删除物理集合/索引。
     */
    void deleteCollection(AiVecSourceEntity source, AiVecStoreEntity store);

    /**
     * 打开面向指定集合的 LangChain4j {@link EmbeddingStore}，供 RAG 与入库共用。
     */
    EmbeddingStore<TextSegment> openEmbeddingStore(AiVecSourceEntity source, AiVecStoreEntity store);
}
