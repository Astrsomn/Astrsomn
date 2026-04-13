package org.astrsomn.core.common.langchain.extension.vector;

import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecSegmentEntity;

/**
 * 集合内的一篇业务文档及与向量侧的映射（文档 id、同步状态等）。
 */
public interface VecDoc {

    VecStore getStore();

    AiVecDocEntity getEntity();

    /**
     * 删除该文档在向量库中对应的全部向量（实现可能按 metadata 过滤或批量 id）。
     */
    void deleteAllEmbeddingsInStore();

    /**
     * 绑定单条切片，用于点删、对账等。
     */
    VecSegment bindSegment(AiVecSegmentEntity segment);
}
