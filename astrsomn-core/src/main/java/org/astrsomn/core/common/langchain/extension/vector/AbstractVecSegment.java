package org.astrsomn.core.common.langchain.extension.vector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.entity.AiVecSegmentEntity;


@Getter
@AllArgsConstructor
public abstract class AbstractVecSegment implements VecSegment {

    private final AbstractVecDoc doc;
    private final AiVecSegmentEntity entity;

    @Override
    public abstract void deleteEmbedding();
}
