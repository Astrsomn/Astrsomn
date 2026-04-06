package org.astrsomn.vector.qdrant;

import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;
import org.astrsomn.core.common.langchain.vector.AbstractVecSource;
import org.astrsomn.core.common.langchain.vector.AbstractVecStore;
import org.astrsomn.vector.qdrant.internal.QdrantAdminClient;
import org.astrsomn.vector.qdrant.internal.QdrantConnectionParams;

public class QdrantVecSource extends AbstractVecSource {

    public QdrantVecSource(AiVecSourceEntity entity) {
        super(entity);
    }

    @Override
    public String getExtensionKey() {
        return QdrantVecStoreBackend.EXTENSION_KEY;
    }

    @Override
    public void testConnection() {
        QdrantConnectionParams p = QdrantConnectionParams.from(getEntity());
        new QdrantAdminClient(p).health();
    }

    @Override
    public AbstractVecStore openStore(AiVecStoreEntity store) {
        return new QdrantVecStore(this, store);
    }
}
