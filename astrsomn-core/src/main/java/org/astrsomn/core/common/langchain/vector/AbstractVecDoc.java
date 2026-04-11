package org.astrsomn.core.common.langchain.vector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecSegmentEntity;


@Getter
@AllArgsConstructor
public abstract class AbstractVecDoc {
    private final AbstractVecStore store;
    private final AiVecDocEntity entity;


    /**
     * 删除该文档在向量库中对应的全部向量（实现可能按 metadata 过滤或批量 id）。
     */
    public abstract void deleteAllEmbeddingsInStore();

    /**
     * 绑定单条切片，用于点删、对账等。
     */
    public abstract AbstractVecSegment bindSegment(AiVecSegmentEntity segment);
}
