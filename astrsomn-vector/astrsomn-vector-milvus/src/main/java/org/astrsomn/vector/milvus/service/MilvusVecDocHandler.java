package org.astrsomn.vector.milvus.service;

import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecSegmentEntity;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecDoc;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecSegment;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.vector.milvus.internal.MilvusVecConstants;

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
