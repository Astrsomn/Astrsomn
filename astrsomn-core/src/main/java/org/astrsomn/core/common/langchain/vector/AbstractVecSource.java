package org.astrsomn.core.common.langchain.vector;

import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;

/**
 * 向量数据源抽象：对应 {@link AiVecSourceEntity}，子模块在此封装与外部向量服务的连接能力（健康检查、向下打开集合等）。
 */
public abstract class AbstractVecSource {

    private final AiVecSourceEntity entity;

    protected AbstractVecSource(AiVecSourceEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("AiVecSourceEntity is required");
        }
        this.entity = entity;
    }

    public AiVecSourceEntity getEntity() {
        return entity;
    }

    /**
     * 与扩展 / provider 一致的标识，如 {@code qdrant}。
     */
    public abstract String getExtensionKey();

    /**
     * 校验与向量服务的网络与鉴权是否可用。
     */
    public abstract void testConnection();

    /**
     * 基于当前数据源打开一个集合层句柄，用于集合生命周期与 {@link dev.langchain4j.store.embedding.EmbeddingStore}。
     */
    public abstract AbstractVecStore openStore(AiVecStoreEntity store);
}
