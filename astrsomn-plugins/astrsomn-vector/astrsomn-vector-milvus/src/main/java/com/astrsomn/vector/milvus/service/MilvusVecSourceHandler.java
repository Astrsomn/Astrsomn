package com.astrsomn.vector.milvus.service;

import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;
import com.astrsomn.api.runtime.common.entity.AiVecStoreEntity;
import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecSource;
import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecStore;
import com.astrsomn.api.runtime.common.langchain.extension.vector.support.AiVecSourceConnectionProperties;
import com.astrsomn.common.utils.StringUtils;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.GetVersionResponse;
import io.milvus.param.ConnectParam;
import io.milvus.param.R;

public final class MilvusVecSourceHandler extends AbstractVecSource {

    private final AiVecSourceConnectionProperties connectionProperties;
    private final MilvusServiceClient milvusClient;

    public MilvusVecSourceHandler(AiVecSourceEntity entity) {
        super(entity);
        this.connectionProperties = AiVecSourceConnectionProperties.from(entity);
        ConnectParam.Builder cb = ConnectParam.newBuilder()
                .withHost(connectionProperties.resolvedHost())
                .withPort(connectionProperties.resolvedPort(19530));
        String user = StringUtils.isNotBlank(connectionProperties.getUsername()) ? connectionProperties.getUsername() : "";
        String pass = StringUtils.isNotBlank(connectionProperties.getPassword()) ? connectionProperties.getPassword() : "";
        cb.withAuthorization(user, pass);
        if (StringUtils.isNotBlank(connectionProperties.getToken())) {
            cb.withToken(connectionProperties.getToken());
        }
        if (StringUtils.isNotBlank(connectionProperties.getDatabaseName())) {
            cb.withDatabaseName(connectionProperties.getDatabaseName());
        }
        this.milvusClient = new MilvusServiceClient(cb.build());
    }

    AiVecSourceConnectionProperties connectionProperties() {
        return connectionProperties;
    }

    MilvusServiceClient milvusClient() {
        return milvusClient;
    }

    @Override
    public boolean testConnection() {
        R<GetVersionResponse> r = milvusClient.getVersion();
        if (r.getStatus() != R.Status.Success.getCode()) {
            throw new IllegalStateException("Milvus testConnection failed: " + r.getMessage());
        }
        return false;
    }

    @Override
    public void shutdown() {
        milvusClient.close();
    }

    @Override
    public AbstractVecStore openStore(AiVecStoreEntity store) {
        return new MilvusVecStoreHandler(this, store);
    }
}
