package org.astrsomn.vector.qdrant.service;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecSource;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecStore;
import org.astrsomn.core.common.langchain.extension.vector.support.AiVecSourceConnectionProperties;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.vector.qdrant.internal.QdrantConfigSupport;

public final class QdrantVecSourceHandler extends AbstractVecSource {

    private final AiVecSourceConnectionProperties connectionProperties;
    private final boolean useTls;
    private final QdrantClient qdrantClient;

    public QdrantVecSourceHandler(AiVecSourceEntity entity) {
        super(entity);
        this.connectionProperties = AiVecSourceConnectionProperties.from(entity);
        this.useTls = QdrantConfigSupport.readUseTls(entity.getConfigJson());
        QdrantGrpcClient.Builder grpc = QdrantGrpcClient.newBuilder(
                connectionProperties.resolvedHost(),
                connectionProperties.resolvedPort(6334),
                useTls);
        if (StringUtils.isNotBlank(connectionProperties.getToken())) {
            grpc.withApiKey(connectionProperties.getToken());
        }
        this.qdrantClient = new QdrantClient(grpc.build());
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
        try {
            qdrantClient.listCollectionsAsync().get();
            return true;
        } catch (Exception e) {
            throw new IllegalStateException("Qdrant testConnection failed", e);
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
