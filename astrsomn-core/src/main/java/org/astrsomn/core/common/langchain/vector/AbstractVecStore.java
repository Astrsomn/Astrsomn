package org.astrsomn.core.common.langchain.vector;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;

@Getter
@AllArgsConstructor
public abstract class AbstractVecStore {

    private final AbstractVecSource source;
    private final AiVecStoreEntity entity;

    // 创建向量侧物理集合
    public abstract void createCollection();

    // 删除向量侧物理集合
    public abstract void dropCollection();

    // 判断是否存在
    public abstract boolean exists();

    // 统计数量
    public abstract long count();

    // 打开 LangChain4j 的嵌入存储，供检索与写入。
    public abstract EmbeddingStore<TextSegment> getEmbeddingStore();

    // 绑定业务文档实体，进入文档级操作。
    public abstract AbstractVecDoc bindDoc(AiVecDocEntity doc);
}
