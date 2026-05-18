package com.astrsomn.vector.qdrant.service;

import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecSource;
import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecStore;
import com.astrsomn.api.runtime.common.langchain.extension.vector.support.AiVecSourceConnectionProperties;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.vector.qdrant.internal.QdrantConfigSupport;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class QdrantVecSourceHandler extends AbstractVecSource {

    private static final Logger log = LoggerFactory.getLogger(QdrantVecSourceHandler.class);

    private final AiVecSourceConnectionProperties connectionProperties;
    private final boolean useTls;
    private final QdrantClient qdrantClient;

    public QdrantVecSourceHandler(AiVecSourceEntity entity) {
        super(entity);
        log.info("[Qdrant] constructor: start");
        this.connectionProperties = AiVecSourceConnectionProperties.from(entity);
        log.info("[Qdrant] constructor: AiVecSourceConnectionProperties parsed");
        this.useTls = QdrantConfigSupport.readUseTls(entity.getConfigJson());
        boolean checkCompatibility = QdrantConfigSupport.readCheckCompatibility(entity.getConfigJson());
        String resolvedHost = connectionProperties.resolvedHost();
        int resolvedPort = connectionProperties.resolvedPort(6334);
        log.info(
                "[Qdrant] constructor: resolved endpoint host={}, port={}, useTls={}, checkCompatibility={}",
                resolvedHost,
                resolvedPort,
                useTls,
                checkCompatibility);
        QdrantGrpcClient.Builder grpc =
                QdrantGrpcClient.newBuilder(resolvedHost, resolvedPort, useTls, checkCompatibility);
        if (StringUtils.isNotBlank(connectionProperties.getToken())) {
            grpc.withApiKey(connectionProperties.getToken());
        }
        log.info("[Qdrant] constructor: calling grpc.build() (may block on channel init) ...");
        var grpcClient = grpc.build();
        log.info("[Qdrant] constructor: grpc.build() done, creating QdrantClient ...");
        this.qdrantClient = new QdrantClient(grpcClient);
        log.info(
                "[Qdrant] client created: host={}, port={}, tls={}, apiKeyConfigured={}",
                resolvedHost,
                resolvedPort,
                useTls,
                StringUtils.isNotBlank(connectionProperties.getToken()));
    }

    private static String qdrantTestFailureHint(Exception e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof java.net.ConnectException) {
                return "Qdrant gRPC 连接失败（请从运行本服务的主机访问 TCP "
                        + "6334；浏览器能打开 6333 仅说明 REST 可达，与 gRPC 端口是否放行无关）: "
                        + t.getMessage();
            }
            String msg = t.getMessage();
            if (msg != null && msg.contains("Connection timed out")) {
                return "Qdrant gRPC 连接超时（请检查防火墙、Docker 是否映射 6334、Qdrant 是否对外监听 gRPC）: " + msg;
            }
        }
        return "Qdrant testConnection failed";
    }

    AiVecSourceConnectionProperties connectionProperties() {
        return connectionProperties;
    }

    boolean useTls() {
        return useTls;
    }

    QdrantClient qdrantClient() {
        return qdrantClient;
    }

    @Override
    public boolean testConnection() {
        long t0 = System.nanoTime();
        log.info("[Qdrant] testConnection: listCollections via gRPC");
        try {
            qdrantClient.listCollectionsAsync().get();
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            log.info("[Qdrant] testConnection success in {}ms", elapsedMs);
            return true;
        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            log.error("[Qdrant] testConnection failed after {}ms", elapsedMs, e);
            throw new IllegalStateException(qdrantTestFailureHint(e), e);
        }
    }

    @Override
    public void shutdown() {
        qdrantClient.close();
    }

    @Override
    public AbstractVecStore openStore(AiVecStoreEntity store) {
        return new QdrantVecStoreHandler(this, store);
    }
}
