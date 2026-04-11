package org.astrsomn.core.common.langchain.vector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.entity.AiVecSegmentEntity;


@Getter
@AllArgsConstructor
public abstract class AbstractVecSegment {

    private final AbstractVecDoc doc;
    private final AiVecSegmentEntity entity;

    /**
     * 按向量 id 删除向量库中的单条记录。
     */
    public abstract void deleteEmbedding();
}
