package com.astrsomn.vector.milvus.service;

import com.astrsomn.api.vector.entity.AiVecDocEntity;
import com.astrsomn.api.vector.entity.AiVecSegmentEntity;
import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecDoc;
import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecSegment;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.vector.milvus.internal.MilvusVecConstants;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;

public final class MilvusVecDocHandler extends AbstractVecDoc {

    public MilvusVecDocHandler(MilvusVecStoreHandler store, AiVecDocEntity entity) {
        super(store, entity);
    }

    @Override
    public MilvusVecStoreHandler getStore() {
        return (MilvusVecStoreHandler) super.getStore();
    }

    @Override
    public void deleteAllEmbeddingsInStore() {
        String docKey = getEntity().getDocIdInStore();
        if (StringUtils.isBlank(docKey)) {
            throw new IllegalStateException("AiVecDocEntity.docIdInStore is required for Milvus deleteAllEmbeddingsInStore");
        }
        getStore()
                .getEmbeddingStore()
                .removeAll(MetadataFilterBuilder.metadataKey(MilvusVecConstants.META_DOC_ID_IN_STORE)
                        .isEqualTo(docKey.trim()));
    }

    @Override
    public AbstractVecSegment bindSegment(AiVecSegmentEntity segment) {
        return new MilvusVecSegmentHandler(this, segment);
    }
}
