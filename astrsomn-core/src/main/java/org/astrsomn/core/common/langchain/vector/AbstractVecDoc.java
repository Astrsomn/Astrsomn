package org.astrsomn.core.common.langchain.vector;

import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecSegmentEntity;

/**
 * 向量文档抽象：对应 {@link AiVecDocEntity}，隶属于某一 {@link AbstractVecStore}。
 * <p>
 * 典型职责：按文档维度删除向量侧数据、同步状态与下游 id（{@code DOC_ID_IN_STORE}）等。
 */
public abstract class AbstractVecDoc {

    private final AbstractVecStore store;
    private final AiVecDocEntity entity;

    protected AbstractVecDoc(AbstractVecStore store, AiVecDocEntity entity) {
        if (store == null || entity == null) {
            throw new IllegalArgumentException("AbstractVecStore and AiVecDocEntity are required");
        }
        this.store = store;
        this.entity = entity;
    }

    public AbstractVecStore getStore() {
        return store;
    }

    public AiVecDocEntity getEntity() {
        return entity;
    }

    /**
     * 删除该文档在向量库中对应的全部向量（实现可能按 metadata 过滤或批量 id）。
     */
    public abstract void deleteAllEmbeddingsInStore();

    /**
     * 绑定单条切片，用于点删、对账等。
     */
    public abstract AbstractVecSegment bindSegment(AiVecSegmentEntity segment);
}
