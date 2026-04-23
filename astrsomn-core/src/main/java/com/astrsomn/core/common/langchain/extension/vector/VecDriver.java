package com.astrsomn.core.common.langchain.extension.vector;

import com.astrsomn.core.common.langchain.extension.model.ModelProviderHandler;
import com.astrsomn.core.common.entity.AiVecDriverEntity;
import com.astrsomn.core.common.entity.AiVecSourceEntity;

/**
 * 向量库插件入口：由 SPI / Spring 注册表按 {@link #getExtensionKey()} 与
 * {@link AiVecSourceEntity#getProvider()} 对齐后解析，再 {@link #bindSource} 得到分层句柄。
 * <p>
 * 角色类比 {@link ModelProviderHandler}。
 */
public interface VecDriver {

    /**
     * 与数据源 {@code provider} 或扩展元数据一致的实现键（如 {@code qdrant}、{@code milvus}）。
     */
    String getExtensionKey();

    /**
     * 基于持久化实体绑定一条连接句柄。
     */
    VecSource bindSource(AiVecSourceEntity source);

    default String getVersion() {
        return "1.0.0";
    }

    default String getAuthor() {
        return "Astrsomn";
    }

    AiVecDriverEntity getDriverEntity();
}
