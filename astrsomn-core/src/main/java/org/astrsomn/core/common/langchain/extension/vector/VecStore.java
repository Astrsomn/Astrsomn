package org.astrsomn.core.common.langchain.extension.vector;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;

/**
 * 逻辑集合（collection / index）：维度、距离度量、物理集合名等，并暴露 LangChain4j 嵌入存储。
 */
public interface VecStore {

    VecSource getSource();

    AiVecStoreEntity getEntity();

    /** 在向量侧创建物理集合（若已存在，实现可选择幂等或抛错，需在实现中约定）。 */
    void createCollection();

    /** 删除向量侧物理集合。 */
    void dropCollection();

    /** 判断物理集合是否存在。 */
    boolean exists();

    /** 集合内向量条数。 */
    long count();

    /**
     * 打开 LangChain4j 的嵌入存储，供检索与写入。
     */
    EmbeddingStore<TextSegment> getEmbeddingStore();

    /**
     * 绑定业务文档实体，进入文档级操作。
     */
    VecDoc bindDoc(AiVecDocEntity doc);
}
