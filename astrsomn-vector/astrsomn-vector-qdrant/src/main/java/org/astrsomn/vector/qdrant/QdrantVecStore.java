package org.astrsomn.vector.qdrant;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;
import org.astrsomn.core.common.langchain.vector.AbstractVecDoc;
import org.astrsomn.core.common.langchain.vector.AbstractVecStore;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.vector.qdrant.internal.QdrantAdminClient;
import org.astrsomn.vector.qdrant.internal.QdrantConnectionParams;

public class QdrantVecStore extends AbstractVecStore {

    private final QdrantConnectionParams connectionParams;

    public QdrantVecStore(QdrantVecSource source, AiVecStoreEntity entity) {
        super(source, entity);
        this.connectionParams = QdrantConnectionParams.from(source.getEntity());
    }

    QdrantAdminClient adminClient() {
        return new QdrantAdminClient(connectionParams);
    }

    QdrantConnectionParams connectionParams() {
        return connectionParams;
    }

    @Override
    public void createOrUpdatePhysicalCollection() {
        AiVecStoreEntity store = getEntity();
        if (StringUtils.isEmpty(store.getCollectionName())) {
            throw new IllegalArgumentException("AiVecStore.collectionName is required");
        }
        int dim = store.getDimension() == null ? 0 : store.getDimension().intValue();
        if (dim <= 0) {
            throw new IllegalArgumentException("AiVecStore.dimension must be positive");
        }
        String distance = QdrantVecStoreBackend.mapDistance(store.getDistanceMetric());
        adminClient().createCollection(store.getCollectionName().trim(), dim, distance);
    }

    @Override
    public void deletePhysicalCollection() {
        AiVecStoreEntity store = getEntity();
        if (StringUtils.isEmpty(store.getCollectionName())) {
            return;
        }
        adminClient().deleteCollection(store.getCollectionName().trim());
    }

    @Override
    public EmbeddingStore<TextSegment> openEmbeddingStore() {
        AiVecStoreEntity store = getEntity();
        if (StringUtils.isEmpty(store.getCollectionName())) {
            throw new IllegalArgumentException("AiVecStore.collectionName is required");
        }
        var builder = QdrantEmbeddingStore.builder()
                .host(connectionParams.host())
                .port(connectionParams.grpcOrRestPortForEmbeddingStore())
                .collectionName(store.getCollectionName().trim());
        if (StringUtils.isNotEmpty(connectionParams.apiKey())) {
            builder.apiKey(connectionParams.apiKey());
        }
        if (connectionParams.useTls()) {
            builder.useTls(true);
        }
        return builder.build();
    }

    @Override
    public AbstractVecDoc bindDoc(AiVecDocEntity doc) {
        return new QdrantVecDoc(this, doc);
    }
}
