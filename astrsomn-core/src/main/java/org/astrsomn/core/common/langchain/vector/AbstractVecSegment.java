package org.astrsomn.core.common.langchain.vector;

import org.astrsomn.core.common.entity.AiVecSegmentEntity;

/**
 * 向量切片抽象：对应 {@link AiVecSegmentEntity}，隶属于某一 {@link AbstractVecDoc}。
 * <p>
 * {@link AiVecSegmentEntity#getVectorId()} 与向量库内 point/embedding id 对齐。
 */
public abstract class AbstractVecSegment {

    private final AbstractVecDoc doc;
    private final AiVecSegmentEntity entity;

    protected AbstractVecSegment(AbstractVecDoc doc, AiVecSegmentEntity entity) {
        if (doc == null || entity == null) {
            throw new IllegalArgumentException("AbstractVecDoc and AiVecSegmentEntity are required");
        }
        this.doc = doc;
        this.entity = entity;
    }

    public AbstractVecDoc getDoc() {
        return doc;
    }

    public AiVecSegmentEntity getEntity() {
        return entity;
    }

    /**
     * 按向量 id 删除向量库中的单条记录。
     */
    public abstract void deleteEmbedding();
}
