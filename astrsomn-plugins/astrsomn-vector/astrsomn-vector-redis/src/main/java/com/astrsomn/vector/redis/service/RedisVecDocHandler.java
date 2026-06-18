package com.astrsomn.vector.redis.service;

import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecDoc;
import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecSegment;
import com.astrsomn.api.vector.entity.AiVecDocEntity;
import com.astrsomn.api.vector.entity.AiVecSegmentEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.vector.redis.internal.RedisVecConstants;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;

public final class RedisVecDocHandler extends AbstractVecDoc {

    public RedisVecDocHandler(RedisVecStoreHandler store, AiVecDocEntity entity) {
        super(store, entity);
    }

    @Override
    public RedisVecStoreHandler getStore() {
        return (RedisVecStoreHandler) super.getStore();
    }

    @Override
    public void deleteAllEmbeddingsInStore() {
        String docKey = getEntity().getDocIdInStore();
        if (StringUtils.isBlank(docKey)) {
            throw new IllegalStateException(
                    "AiVecDocEntity.docIdInStore is required for Redis deleteAllEmbeddingsInStore");
        }
        getStore()
                .getEmbeddingStore()
                .removeAll(MetadataFilterBuilder.metadataKey(RedisVecConstants.META_DOC_ID_IN_STORE)
                        .isEqualTo(docKey.trim()));
    }

    @Override
    public AbstractVecSegment bindSegment(AiVecSegmentEntity segment) {
        return new RedisVecSegmentHandler(this, segment);
    }
}
