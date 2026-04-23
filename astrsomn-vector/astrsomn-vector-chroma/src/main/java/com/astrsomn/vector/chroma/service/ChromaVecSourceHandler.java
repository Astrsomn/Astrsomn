package com.astrsomn.vector.chroma.service;

import dev.langchain4j.store.embedding.chroma.ChromaApiVersion;
import com.astrsomn.core.common.entity.AiVecSourceEntity;
import com.astrsomn.core.common.entity.AiVecStoreEntity;
import com.astrsomn.core.common.langchain.extension.vector.AbstractVecSource;
import com.astrsomn.core.common.langchain.extension.vector.AbstractVecStore;
import com.astrsomn.core.common.langchain.extension.vector.support.AiVecSourceConnectionProperties;
import com.astrsomn.core.common.utils.StringUtils;
import com.astrsomn.vector.chroma.internal.ChromaConfigSupport;
import com.astrsomn.vector.chroma.internal.ChromaEmbeddingStores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChromaVecSourceHandler extends AbstractVecSource {

    private static final Logger log = LoggerFactory.getLogger(ChromaVecSourceHandler.class);

    private final AiVecSourceConnectionProperties connectionProperties;
    private final String baseUrl;
    private final ChromaApiVersion apiVersion;
    private final String apiKey;

    public ChromaVecSourceHandler(AiVecSourceEntity entity) {
        super(entity);
        log.info("[Chroma] constructor: start");
        this.connectionProperties = AiVecSourceConnectionProperties.from(entity);
        log.info("[Chroma] constructor: AiVecSourceConnectionProperties parsed");
        this.baseUrl = ChromaEmbeddingStores.resolveBaseUrl(entity);
        this.apiVersion = ChromaConfigSupport.readApiVersion(entity.getConfigJson());
        this.apiKey = connectionProperties.getToken();
        log.info(
                "[Chroma] client configured: baseUrl={}, apiVersion={}, useTls={}, apiKeyConfigured={}",
                baseUrl,
                apiVersion,
                ChromaConfigSupport.readUseTls(entity.getConfigJson()),
                StringUtils.isNotBlank(apiKey));
    }

    AiVecSourceConnectionProperties connectionProperties() {
        return connectionProperties;
    }

    String baseUrl() {
        return baseUrl;
    }

    ChromaApiVersion apiVersion() {
        return apiVersion;
    }

    String apiKey() {
        return apiKey;
    }

    @Override
    public boolean testConnection() {
        long t0 = System.nanoTime();
        log.info("[Chroma] testConnection: creating test embedding store (apiVersion={})", apiVersion);
        try {
            ChromaEmbeddingStores.buildForCollection(getEntity(), "test_connection");
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            log.info("[Chroma] testConnection success in {}ms", elapsedMs);
            return true;
        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            log.error("[Chroma] testConnection failed after {}ms", elapsedMs, e);
            throw new IllegalStateException(chromaTestFailureHint(e), e);
        }
    }

    private static String chromaTestFailureHint(Exception e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof java.net.ConnectException) {
                return "Chroma HTTP 连接失败（请从运行本服务的主机访问 Chroma 端口）: " + t.getMessage();
            }
            String msg = t.getMessage();
            if (msg != null && msg.contains("Connection timed out")) {
                return "Chroma HTTP 连接超时（请检查防火墙、Docker 端口映射、Chroma 是否对外监听）: " + msg;
            }
            if (msg != null && (msg.contains("HTTP error") || msg.contains("410") || msg.contains("404"))) {
                return "Chroma HTTP 调用失败（Chroma 0.7+ 仅支持 REST API V2；请在 CONFIG_JSON 设置 \"apiVersion\":\"V2\" 或留空使用默认 V2；"
                        + "若仍失败请核对 tenantName、databaseName、TLS(useTls) 与 Chroma 版本）: "
                        + msg;
            }
        }
        return "Chroma testConnection failed（若服务端为 Chroma 0.7+，请使用 API V2；浏览器能打开 /docs 不代表 V1 客户端仍可用）";
    }

    @Override
    public void shutdown() {
        // ChromaEmbeddingStore 会在内部管理连接，不需要显式关闭
    }

    @Override
    public AbstractVecStore openStore(AiVecStoreEntity store) {
        return new ChromaVecStoreHandler(this, store);
    }
}
