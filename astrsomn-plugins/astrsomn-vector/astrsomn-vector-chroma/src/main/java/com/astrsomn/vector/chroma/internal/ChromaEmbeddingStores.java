package com.astrsomn.vector.chroma.internal;

import dev.langchain4j.store.embedding.chroma.ChromaApiVersion;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;
import com.astrsomn.api.runtime.common.langchain.extension.vector.support.AiVecSourceConnectionProperties;
import com.astrsomn.common.utils.StringUtils;

/**
 * 统一构建 {@link ChromaEmbeddingStore}，保证 testConnection、集合 CRUD、向量读写使用同一套连接参数（含 API V2、租户、库、TLS）。
 */
public final class ChromaEmbeddingStores {

    private ChromaEmbeddingStores() {}

    /** 与 {@link #buildForCollection} 一致的 HTTP(S) 基址，用于日志与诊断。 */
    public static String resolveBaseUrl(AiVecSourceEntity entity) {
        AiVecSourceConnectionProperties conn = AiVecSourceConnectionProperties.from(entity);
        boolean useTls = ChromaConfigSupport.readUseTls(entity.getConfigJson());
        String scheme = useTls ? "https" : "http";
        return scheme + "://" + conn.resolvedHost() + ":" + conn.resolvedPort(8000);
    }

    public static ChromaEmbeddingStore buildForCollection(AiVecSourceEntity entity, String collectionName) {
        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("collectionName is required for ChromaEmbeddingStore");
        }
        AiVecSourceConnectionProperties conn = AiVecSourceConnectionProperties.from(entity);
        String baseUrl = resolveBaseUrl(entity);
        ChromaApiVersion apiVersion = ChromaConfigSupport.readApiVersion(entity.getConfigJson());

        ChromaEmbeddingStore.Builder b = ChromaEmbeddingStore.builder()
                .apiVersion(apiVersion)
                .baseUrl(baseUrl)
                .collectionName(collectionName.trim());

        if (apiVersion == ChromaApiVersion.V2) {
            String tenant = ChromaConfigSupport.readTenantName(entity.getConfigJson());
            if (StringUtils.isNotBlank(tenant)) {
                b.tenantName(tenant);
            }
            String db = conn.getDatabaseName();
            if (StringUtils.isNotBlank(db)) {
                b.databaseName(db.trim());
            }
        }

        return b.build();
    }
}
