package com.astrsomn.api.runtime.common.langchain.extension.vector;

import com.astrsomn.api.runtime.common.entity.AiVecSegmentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public abstract class AbstractVecSegment implements VecSegment {

    private final AbstractVecDoc doc;
    private final AiVecSegmentEntity entity;

    @Override
    public abstract void deleteEmbedding();
}