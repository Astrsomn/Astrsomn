package org.astrsomn.vector.qdrant.service;

import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecSegmentEntity;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecDoc;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecSegment;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.vector.qdrant.internal.QdrantVecConstants;

public final class QdrantVecDocHandler extends AbstractVecDoc {

    public QdrantVecDocHandler(QdrantVecStoreHandler store, AiVecDocEntity entity) {
        super(store, entity);
    }

    @Override
    public QdrantVecStoreHandler getStore() {
        return (QdrantVecStoreHandler) super.getStore();
    }

    @Override
    public void deleteAllEmbeddingsInStore() {
        String docKey = getEntity().getDocIdInStore();
        if (StringUtils.isBlank(docKey)) {
            throw new IllegalStateException("AiVecDocEntity.docIdInStore is required for Qdrant deleteAllEmbeddingsInStore");
        }
        getStore()
                .getEmbeddingStore()
                .removeAll(MetadataFilterBuilder.metadataKey(QdrantVecConstants.META_DOC_ID_IN_STORE)
                        .isEqualTo(docKey.trim()));
    }

    @Override
    public AbstractVecSegment bindSegment(AiVecSegmentEntity segment) {
        return new QdrantVecSegmentHandler(this, segment);
    }
}
