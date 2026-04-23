package com.astrsomn.vector.chroma.service;

import com.astrsomn.core.common.entity.AiVecSegmentEntity;
import com.astrsomn.core.common.langchain.extension.vector.AbstractVecSegment;
import com.astrsomn.core.common.utils.StringUtils;

public final class ChromaVecSegmentHandler extends AbstractVecSegment {

    public ChromaVecSegmentHandler(ChromaVecDocHandler doc, AiVecSegmentEntity entity) {
        super(doc, entity);
    }

    @Override
    public ChromaVecDocHandler getDoc() {
        return (ChromaVecDocHandler) super.getDoc();
    }

    @Override
    public void deleteEmbedding() {
        String id = getEntity().getVectorId();
        if (StringUtils.isBlank(id)) {
            throw new IllegalStateException("AiVecSegmentEntity.vectorId is required for Chroma deleteEmbedding");
        }
        getDoc().getStore().getEmbeddingStore().remove(id.trim());
    }
}