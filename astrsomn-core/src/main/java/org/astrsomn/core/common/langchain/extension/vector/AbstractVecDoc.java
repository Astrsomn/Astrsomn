package org.astrsomn.core.common.langchain.extension.vector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecSegmentEntity;


@Getter
@AllArgsConstructor
public abstract class AbstractVecDoc implements VecDoc {

    private final AbstractVecStore store;
    private final AiVecDocEntity entity;

    @Override
    public abstract void deleteAllEmbeddingsInStore();

    @Override
    public abstract AbstractVecSegment bindSegment(AiVecSegmentEntity segment);
}
