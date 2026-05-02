package com.astrsomn.api.runtime.common.langchain.extension.vector;

import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;
import com.astrsomn.api.runtime.common.entity.AiVecStoreEntity;

/**
 * 向量数据源句柄：一条向量服务连接（host、鉴权、配置等），可打开多个 {@link VecStore}。
 * <p>
 * 插件实现通常由 {@link VecDriver#bindSource(AiVecSourceEntity)} 创建。
 */
public interface VecSource {

    AiVecSourceEntity getEntity();

    /**
     * 校验与向量服务的网络与鉴权是否可用。
     */
    boolean testConnection();

    /**
     * 资源释放（插件卸载或服务关闭时调用）。
     */
    void shutdown();

    /**
     * 基于当前数据源打开集合层句柄，用于集合生命周期与 LangChain4j {@link dev.langchain4j.store.embedding.EmbeddingStore}。
     */
    VecStore openStore(AiVecStoreEntity store);
}
