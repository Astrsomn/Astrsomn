package org.astrsomn.starter.langchain.tool.rag.impl;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.astrsomn.core.common.constant.VectorStoreType;
import org.astrsomn.core.common.langchain.buildParam.setting.RagSetting;

public interface VectorStoreHandler {
    /**
     * 判断当前 Handler 是否能处理该类型的向量库
     */
    boolean supports(VectorStoreType type);

    /**
     * 根据配置创建或获取 EmbeddingStore
     * @param config 包含连接信息、Index/Collection名称等
     */
    EmbeddingStore<TextSegment> getStore(RagSetting config);
}
