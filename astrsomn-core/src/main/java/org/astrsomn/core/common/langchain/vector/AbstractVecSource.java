package org.astrsomn.core.common.langchain.vector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;


@Getter
@AllArgsConstructor
public abstract class AbstractVecSource {

    private final AiVecSourceEntity entity;



    /**
     * 校验与向量服务的网络与鉴权是否可用。
     */
    public abstract void testConnection();

    /**
     * 资源释放（插件卸载或服务关闭时调用）
     */
    public abstract void shutdown();

    /**
     * 基于当前数据源打开一个集合层句柄，用于集合生命周期与 {@link dev.langchain4j.store.embedding.EmbeddingStore}。
     */
    public abstract AbstractVecStore openStore(AiVecStoreEntity store);


}
