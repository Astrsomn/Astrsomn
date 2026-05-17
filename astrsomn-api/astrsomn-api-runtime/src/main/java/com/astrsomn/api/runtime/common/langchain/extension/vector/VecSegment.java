package com.astrsomn.api.runtime.common.langchain.extension.vector;

import com.astrsomn.api.vector.entity.AiVecSegmentEntity;


public interface VecSegment {

    VecDoc getDoc();

    AiVecSegmentEntity getEntity();


    void deleteEmbedding();
}