package org.astrsomn.vector.chroma.service;

import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecSource;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecStore;
import org.astrsomn.core.common.langchain.extension.vector.support.AiVecSourceConnectionProperties;
import org.astrsomn.core.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChromaVecSourceHandler extends AbstractVecSource {

    private static final Logger log = LoggerFactory.getLogger(ChromaVecSourceHandler.class);

    private final AiVecSourceConnectionProperties connectionProperties;
    private final String baseUrl;
    private final String apiKey;

    public ChromaVecSourceHandler(AiVecSourceEntity entity) {
        super(entity);
        log.info("[Chroma] constructor: start");
        this.connectionProperties = AiVecSourceConnectionProperties.from(entity);
        log.info("[Chroma] constructor: AiVecSourceConnectionProperties parsed");
        String resolvedHost = connectionProperties.resolvedHost();
        int resolvedPort = connectionProperties.resolvedPort(8000);
        this.baseUrl = "http://" + resolvedHost + ":" + resolvedPort;
        this.apiKey = connectionProperties.getToken();
        log.info("[Chroma] client configured: baseUrl={}, apiKeyConfigured={}", baseUrl, StringUtils.isNotBlank(apiKey));
    }

    AiVecSourceConnectionProperties connectionProperties() {
        return connectionProperties;
    }

    String baseUrl() {
        return baseUrl;
    }

    String apiKey() {
        return apiKey;
    }

    @Override
    public boolean testConnection() {
        long t0 = System.nanoTime();
        log.info("[Chroma] testConnection: creating test embedding store");
        try {
            // 创建一个临时的 embedding store 来测试连接
            ChromaEmbeddingStore.builder()
                    .baseUrl(baseUrl)

                    .collectionName("test_connection")
                    .build();
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
                return "Chroma HTTP 连接失败（请从运行本服务的主机访问 TCP 8000）: " + t.getMessage();
            }
            String msg = t.getMessage();
            if (msg != null && msg.contains("Connection timed out")) {
                return "Chroma HTTP 连接超时（请检查防火墙、Docker 是否映射 8000、Chroma 是否对外监听）: " + msg;
            }
        }
        return "Chroma testConnection failed";
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