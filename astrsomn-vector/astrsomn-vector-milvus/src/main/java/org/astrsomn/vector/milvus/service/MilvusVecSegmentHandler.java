package org.astrsomn.vector.milvus.service;

import org.astrsomn.core.common.entity.AiVecSegmentEntity;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecSegment;
import org.astrsomn.core.common.util.StringUtils;

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
