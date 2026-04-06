package org.astrsomn.vector.qdrant.config;

import org.astrsomn.core.common.langchain.rag.RagEmbeddingStoreResolver;
import org.astrsomn.core.mapper.AiVecSourceMapper;
import org.astrsomn.core.mapper.AiVecStoreMapper;
import org.astrsomn.vector.qdrant.VecStoreBackendRegistry;
import org.astrsomn.vector.qdrant.rag.DefaultRagEmbeddingStoreResolver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * 向量化运行时：查库并返回 {@link dev.langchain4j.store.embedding.EmbeddingStore}；需 MyBatis 已注册 {@link AiVecStoreMapper}。
 */
@AutoConfiguration
@ConditionalOnBean(AiVecStoreMapper.class)
public class QdrantVectorAutoConfiguration {

    @Bean
    public VecStoreBackendRegistry vecStoreBackendRegistry() {
        return new VecStoreBackendRegistry();
    }

    @Bean
    public RagEmbeddingStoreResolver ragEmbeddingStoreResolver(
            AiVecSourceMapper aiVecSourceMapper,
            AiVecStoreMapper aiVecStoreMapper,
            VecStoreBackendRegistry vecStoreBackendRegistry) {
        return new DefaultRagEmbeddingStoreResolver(aiVecSourceMapper, aiVecStoreMapper, vecStoreBackendRegistry);
    }
}
