package org.astrsomn.vector.qdrant.service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecDoc;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecStore;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.vector.qdrant.internal.QdrantVecConstants;

import java.util.concurrent.ExecutionException;

public final class QdrantVecStoreHandler extends AbstractVecStore {

    private final QdrantVecSourceHandler qdrantSource;
    private volatile EmbeddingStore<TextSegment> embeddingStoreCache;

    public QdrantVecStoreHandler(QdrantVecSourceHandler source, AiVecStoreEntity entity) {
        super(source, entity);
        this.qdrantSource = source;
    }

    QdrantVecSourceHandler qdrantSource() {
        return qdrantSource;
    }

    @Override
    public void createCollection() {
        QdrantClient client = qdrantSource.qdrantClient();
        String name = collectionNameRequired();
        if (exists()) {
            return;
        }
        int dim = dimensionRequired();
        Distance d = mapDistance(getEntity().getDistanceMetric());
        VectorParams params =
                VectorParams.newBuilder().setSize(dim).setDistance(d).build();
        try {
            client.createCollectionAsync(name, params).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("createCollection interrupted", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("createCollection failed: " + name, e.getCause());
        }
    }

    @Override
    public void dropCollection() {
        try {
            qdrantSource.qdrantClient().deleteCollectionAsync(collectionNameRequired()).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("dropCollection interrupted", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("dropCollection failed", e.getCause());
        } finally {
            embeddingStoreCache = null;
        }
    }

    @Override
    public boolean exists() {
        try {
            return qdrantSource.qdrantClient().collectionExistsAsync(collectionNameRequired()).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("exists check interrupted", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("exists check failed", e.getCause());
        }
    }

    @Override
    public long count() {
        try {
            Long n = qdrantSource.qdrantClient().countAsync(collectionNameRequired()).get();
            return n == null ? 0L : n;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("count interrupted", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("count failed", e.getCause());
        }
    }

    @Override
    public EmbeddingStore<TextSegment> getEmbeddingStore() {
        if (embeddingStoreCache == null) {
            synchronized (this) {
                if (embeddingStoreCache == null) {
                    embeddingStoreCache = QdrantEmbeddingStore.builder()
                            .client(qdrantSource.qdrantClient())
                            .collectionName(collectionNameRequired())
                            .payloadTextKey(QdrantVecConstants.DEFAULT_PAYLOAD_TEXT_KEY)
                            .build();
                }
            }
        }
        return embeddingStoreCache;
    }

    @Override
    public AbstractVecDoc bindDoc(AiVecDocEntity doc) {
        return new QdrantVecDocHandler(this, doc);
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

    private static Distance mapDistance(String metric) {
        if (StringUtils.isBlank(metric)) {
            return Distance.Cosine;
        }
        switch (metric.trim().toLowerCase()) {
            case "cosine":
                return Distance.Cosine;
            case "dot":
            case "dot_product":
            case "ip":
                return Distance.Dot;
            case "euclid":
            case "euclidean":
            case "l2":
                return Distance.Euclid;
            default:
                return Distance.Cosine;
        }
    }
}
