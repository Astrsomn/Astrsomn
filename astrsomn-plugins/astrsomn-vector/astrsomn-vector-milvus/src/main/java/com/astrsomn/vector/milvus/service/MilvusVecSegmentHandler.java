package com.astrsomn.vector.milvus.service;

import com.astrsomn.api.runtime.common.entity.AiVecSegmentEntity;
import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecSegment;
import com.astrsomn.common.utils.StringUtils;

import java.util.List;

public final class MilvusVecSegmentHandler extends AbstractVecSegment {

    public MilvusVecSegmentHandler(MilvusVecDocHandler doc, AiVecSegmentEntity entity) {
        super(doc, entity);
    }

    @Override
    public MilvusVecDocHandler getDoc() {
        return (MilvusVecDocHandler) super.getDoc();
    }

    @Override
    public void deleteEmbedding() {
        String id = getEntity().getVectorId();
        if (StringUtils.isBlank(id)) {
            throw new IllegalStateException("AiVecSegmentEntity.vectorId is required for Milvus deleteEmbedding");
        }
        getDoc().getStore().getEmbeddingStore().removeAll(List.of(id.trim()));
    }
}
