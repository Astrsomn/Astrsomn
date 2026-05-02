package com.astrsomn.vector.chroma.service;

import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import com.astrsomn.api.runtime.common.entity.AiVecDocEntity;
import com.astrsomn.api.runtime.common.entity.AiVecSegmentEntity;
import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecDoc;
import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecSegment;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.vector.chroma.internal.ChromaVecConstants;

public final class ChromaVecDocHandler extends AbstractVecDoc {

    public ChromaVecDocHandler(ChromaVecStoreHandler store, AiVecDocEntity entity) {
        super(store, entity);
    }

    @Override
    public ChromaVecStoreHandler getStore() {
        return (ChromaVecStoreHandler) super.getStore();
    }

    @Override
    public void deleteAllEmbeddingsInStore() {
        String docKey = getEntity().getDocIdInStore();
        if (StringUtils.isBlank(docKey)) {
            throw new IllegalStateException("AiVecDocEntity.docIdInStore is required for Chroma deleteAllEmbeddingsInStore");
        }
        getStore()
                .getEmbeddingStore()
                .removeAll(MetadataFilterBuilder.metadataKey(ChromaVecConstants.META_DOC_ID_IN_STORE)
                        .isEqualTo(docKey.trim()));
    }

    @Override
    public AbstractVecSegment bindSegment(AiVecSegmentEntity segment) {
        return new ChromaVecSegmentHandler(this, segment);
    }
}