package com.astrsomn.api.runtime.common.langchain.extension.vector;

import com.astrsomn.api.vector.entity.AiVecDocEntity;
import com.astrsomn.api.vector.entity.AiVecSegmentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;


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