package com.astrsomn.vector.milvus.service;

import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecDoc;
import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecStore;
import com.astrsomn.api.vector.entity.AiVecDocEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.common.utils.StringUtils;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.GetCollectionStatisticsResponse;
import io.milvus.grpc.KeyValuePair;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.DropCollectionParam;
import io.milvus.param.collection.GetCollectionStatisticsParam;
import io.milvus.param.collection.HasCollectionParam;

public final class MilvusVecStoreHandler extends AbstractVecStore {

    private final MilvusVecSourceHandler milvusSource;
    private volatile MilvusEmbeddingStore embeddingStoreCache;

    public MilvusVecStoreHandler(MilvusVecSourceHandler source, AiVecStoreEntity entity) {
        super(source, entity);
        this.milvusSource = source;
    }

    private static MetricType mapMetric(String metric) {
        if (StringUtils.isBlank(metric)) {
            return MetricType.COSINE;
        }
        switch (metric.trim().toLowerCase()) {
            case "l2":
            case "euclidean":
            case "euclid":
                return MetricType.L2;
            case "ip":
            case "dot":
            case "dot_product":
                return MetricType.IP;
            case "cosine":
            default:
                return MetricType.COSINE;
        }
    }

    MilvusVecSourceHandler milvusSource() {
        return milvusSource;
    }

    @Override
    public void createCollection() {
        if (exists()) {
            return;
        }
        synchronized (this) {
            embeddingStoreCache = buildEmbeddingStore();
        }
    }

    @Override
    public void dropCollection() {
        MilvusServiceClient client = milvusSource.milvusClient();
        R<?> r = client.dropCollection(
                DropCollectionParam.newBuilder().withCollectionName(collectionNameRequired()).build());
        if (r.getStatus() != R.Status.Success.getCode()) {
            throw new IllegalStateException("dropCollection failed: " + r.getMessage());
        }
        embeddingStoreCache = null;
    }

    @Override
    public boolean exists() {
        MilvusServiceClient client = milvusSource.milvusClient();
        R<Boolean> r = client.hasCollection(
                HasCollectionParam.newBuilder().withCollectionName(collectionNameRequired()).build());
        if (r.getStatus() != R.Status.Success.getCode()) {
            throw new IllegalStateException("hasCollection failed: " + r.getMessage());
        }
        return Boolean.TRUE.equals(r.getData());
    }

    @Override
    public long count() {
        MilvusServiceClient client = milvusSource.milvusClient();
        R<GetCollectionStatisticsResponse> r = client.getCollectionStatistics(
                GetCollectionStatisticsParam.newBuilder()
                        .withCollectionName(collectionNameRequired())
                        .build());
        if (r.getStatus() != R.Status.Success.getCode()) {
            throw new IllegalStateException("getCollectionStatistics failed: " + r.getMessage());
        }
        GetCollectionStatisticsResponse data = r.getData();
        if (data == null) {
            return 0L;
        }
        for (KeyValuePair kv : data.getStatsList()) {
            if ("row_count".equals(kv.getKey())) {
                try {
                    return Long.parseLong(kv.getValue().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalStateException("Unexpected row_count: " + kv.getValue(), e);
                }
            }
        }
        return 0L;
    }

    @Override
    public EmbeddingStore<TextSegment> getEmbeddingStore() {
        if (embeddingStoreCache == null) {
            synchronized (this) {
                if (embeddingStoreCache == null) {
                    embeddingStoreCache = buildEmbeddingStore();
                }
            }
        }
        return embeddingStoreCache;
    }

    private MilvusEmbeddingStore buildEmbeddingStore() {
        return MilvusEmbeddingStore.builder()
                .milvusClient(milvusSource.milvusClient())
                .collectionName(collectionNameRequired())
                .dimension(dimensionRequired())
                .metricType(mapMetric(getEntity().getDistanceMetric()))
                .build();
    }

    @Override
    public AbstractVecDoc bindDoc(AiVecDocEntity doc) {
        return new MilvusVecDocHandler(this, doc);
    }

    private String collectionNameRequired() {
        String n = getEntity().getCollectionName();
        if (StringUtils.isBlank(n)) {
            throw new IllegalStateException("AiVecStoreEntity.collectionName is required");
        }
        return n.trim();
    }

    private int dimensionRequired() {
        Long d = getEntity().getDimension();
        if (d == null || d <= 0) {
            throw new IllegalStateException("AiVecStoreEntity.dimension must be positive");
        }
        if (d > Integer.MAX_VALUE) {
            throw new IllegalStateException("AiVecStoreEntity.dimension too large");
        }
        return d.intValue();
    }
}
