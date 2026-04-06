package org.astrsomn.core.common.langchain.vector;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;

/**
 * 向量集合抽象：对应 {@link AiVecStoreEntity}（逻辑 collection/index），在某一 {@link AbstractVecSource} 之下。
 */
public abstract class AbstractVecStore {

    private final AbstractVecSource source;
    private final AiVecStoreEntity entity;

    protected AbstractVecStore(AbstractVecSource source, AiVecStoreEntity entity) {
        if (source == null || entity == null) {
            throw new IllegalArgumentException("AbstractVecSource and AiVecStoreEntity are required");
        }
        this.source = source;
        this.entity = entity;
    }

    public AbstractVecSource getSource() {
        return source;
    }

    public AiVecStoreEntity getEntity() {
        return entity;
    }

    /**
     * 在向量侧创建或更新物理集合（维度、距离度量等与 entity 对齐）。
     */
    public abstract void createOrUpdatePhysicalCollection();

    /**
     * 删除向量侧物理集合。
     */
    public abstract void deletePhysicalCollection();

    /**
     * 打开 LangChain4j 的嵌入存储，供检索与写入。
     */
    public abstract EmbeddingStore<TextSegment> openEmbeddingStore();

    /**
     * 绑定业务文档实体，进入文档级操作。
     */
    public abstract AbstractVecDoc bindDoc(AiVecDocEntity doc);
}
