package com.astrsomn.vector.qdrant.service;

import com.astrsomn.core.common.entity.AiVecSegmentEntity;
import com.astrsomn.core.common.langchain.extension.vector.AbstractVecSegment;
import com.astrsomn.core.common.utils.StringUtils;

public final class QdrantVecSegmentHandler extends AbstractVecSegment {

    public QdrantVecSegmentHandler(QdrantVecDocHandler doc, AiVecSegmentEntity entity) {
        super(doc, entity);
    }

    @Override
    public QdrantVecDocHandler getDoc() {
        return (QdrantVecDocHandler) super.getDoc();
    }

    @Override
    public void deleteEmbedding() {
        String id = getEntity().getVectorId();
        if (StringUtils.isBlank(id)) {
            throw new IllegalStateException("AiVecSegmentEntity.vectorId is required for Qdrant deleteEmbedding");
        }
        getDoc().getStore().getEmbeddingStore().remove(id.trim());
    }
}
