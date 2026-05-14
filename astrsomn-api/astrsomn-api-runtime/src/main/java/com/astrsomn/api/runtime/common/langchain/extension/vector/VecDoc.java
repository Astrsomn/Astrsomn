package com.astrsomn.api.runtime.common.langchain.extension.vector;

import com.astrsomn.api.runtime.common.entity.AiVecDocEntity;
import com.astrsomn.api.runtime.common.entity.AiVecSegmentEntity;


public interface VecDoc {

    VecStore getStore();

    AiVecDocEntity getEntity();

    
    void deleteAllEmbeddingsInStore();

    
    VecSegment bindSegment(AiVecSegmentEntity segment);
}