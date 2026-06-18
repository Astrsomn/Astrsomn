package com.astrsomn.vector.redis.service;

import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecSegment;
import com.astrsomn.api.vector.entity.AiVecSegmentEntity;
import com.astrsomn.common.utils.StringUtils;

public final class RedisVecSegmentHandler extends AbstractVecSegment {

    public RedisVecSegmentHandler(RedisVecDocHandler doc, AiVecSegmentEntity entity) {
        super(doc, entity);
    }

    @Override
    public RedisVecDocHandler getDoc() {
        return (RedisVecDocHandler) super.getDoc();
    }

    @Override
    public void deleteEmbedding() {
        String id = getEntity().getVectorId();
        if (StringUtils.isBlank(id)) {
            throw new IllegalStateException(
                    "AiVecSegmentEntity.vectorId is required for Redis deleteEmbedding");
        }
        getDoc().getStore().getEmbeddingStore().remove(id.trim());
    }
}
