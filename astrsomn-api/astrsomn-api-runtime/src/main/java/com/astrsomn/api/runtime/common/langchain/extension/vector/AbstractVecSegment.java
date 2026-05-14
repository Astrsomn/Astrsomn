package com.astrsomn.api.runtime.common.langchain.extension.vector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.api.runtime.common.entity.AiVecSegmentEntity;


@Getter
@AllArgsConstructor
public abstract class AbstractVecSegment implements VecSegment {

    private final AbstractVecDoc doc;
    private final AiVecSegmentEntity entity;

    @Override
    public abstract void deleteEmbedding();
}