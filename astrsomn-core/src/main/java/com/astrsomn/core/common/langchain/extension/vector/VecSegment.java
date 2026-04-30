package com.astrsomn.core.common.langchain.extension.vector;

import com.astrsomn.core.common.entity.AiVecSegmentEntity;

/**
 * 文档拆分后的切片及向量侧记录标识（如 vector id）。
 */
public interface VecSegment {

    VecDoc getDoc();

    AiVecSegmentEntity getEntity();

    /**
     * 按向量 id 删除向量库中的单条记录。
     */
    void deleteEmbedding();
}
